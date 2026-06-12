package net.masterstudios.konze.spring

import jakarta.servlet.http.HttpServletRequest
import net.masterstudios.konze.Engine
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicLong

@RestController
public class SchemaDiscoveryController(
    private val engine: Engine,
    private val handlerMapping: RequestMappingHandlerMapping) {

    private val requestCounts = ConcurrentHashMap<String, AtomicLong>()
    private val lastResetTimestamp = AtomicLong(System.currentTimeMillis())

    public fun getSchemaDiscovery(profilePath: String): String {
        val now = System.currentTimeMillis()
        val lastReset = lastResetTimestamp.get()
        if (now - lastReset > 60000) {
            if (lastResetTimestamp.compareAndSet(lastReset, now)) {
                requestCounts.clear()
            }
        }
        for (context in engine.databaseContexts.values) {
            val pool = context.poolManager.getPoolBySchemaDiscoveryPath(profilePath)
            if (pool != null) {
                val limit = pool.profileConfiguration.schemaDiscoveryEndpoint.rateLimiting
                val count = requestCounts.computeIfAbsent(profilePath) { AtomicLong(0) }
                
                if (count.incrementAndGet() > limit) {
                    throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded for profile path: $profilePath")
                }

                val schemaDiscovery = pool.schemaDiscovery
                return schemaDiscovery?.getSchemaDumpAsString() ?: "Schema discovery not configured for profile path: $profilePath"
            }
        }
        return "Profile not found for path: $profilePath"
    }
    
    fun schemaDiscoveryEndpoint(request: HttpServletRequest): ResponseEntity<String?> {
        return ResponseEntity.ok<String?>(getSchemaDiscovery(request.getRequestURI()))
    }

    @Throws(NoSuchMethodException::class)
    fun registerEndpoint(path: String) {
        val targetMethod = SchemaDiscoveryController::class.java.getMethod("schemaDiscoveryEndpoint", HttpServletRequest::class.java)
        val mappingInfo = RequestMappingInfo
            .paths(path)
            .methods(RequestMethod.GET)
            .options(RequestMappingInfo.BuilderConfiguration()) // Required for path parsing
            .build()
        handlerMapping.registerMapping(mappingInfo, this, targetMethod)
    }

    fun unregisterEndpoint(path: String) {
        val mappingInfo = RequestMappingInfo
            .paths(path)
            .methods(RequestMethod.GET)
            .options(RequestMappingInfo.BuilderConfiguration())
            .build()
        handlerMapping.unregisterMapping(mappingInfo)
    }
}
