package net.masterstudios.konze.spring

import net.masterstudios.konze.Engine
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
public class SchemaDiscoveryController(private val engine: Engine) {

    @GetMapping("/schema-discovery/{profileName}")
    public fun getSchemaDiscovery(@PathVariable profileName: String): String {
        for (context in engine.databaseContexts.values) {
            val pool = context.poolManager.getPool(profileName)
            if (pool != null) {
                val schemaDiscovery = pool.schemaDiscovery
                return schemaDiscovery?.getSchemaDumpAsString() ?: "Schema discovery not configured for profile: $profileName"
            }
        }
        return "Profile not found: $profileName"
    }
}
