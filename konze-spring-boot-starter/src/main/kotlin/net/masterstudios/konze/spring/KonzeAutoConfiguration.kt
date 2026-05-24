package net.masterstudios.konze.spring

import net.masterstudios.konze.Engine
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import javax.sql.DataSource
import kotlin.collections.flatten

@ConfigurationProperties(prefix = "konze")
public data class KonzeProperties(
    public var configFiles: List<String> = emptyList()
)

@Configuration
@EnableConfigurationProperties(KonzeProperties::class)
public open class KonzeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public open fun konzeEngine(properties: KonzeProperties): Engine {
        val paths = properties.configFiles.ifEmpty {
            listOf("example-app/src/main/resources/example-spec.yaml")
        }
        return Engine(paths)
    }

    @Primary
    @Bean("dataSource")
    fun routingDataSource(engine: Engine): DataSource {
        val targets: MutableMap<Any, Any> = mutableMapOf()
        for ((databaseContextId, databaseContext) in engine.databaseContexts) {
            val pools = databaseContext.poolManager.getAllPools()
            for ((poolKey, dataSource) in pools) {
                val compositeKey = "$databaseContextId.$poolKey"
                targets[compositeKey as Any] = dataSource as Any
                if (engine.databaseContexts.size == 1) {
                    targets[poolKey as Any] = dataSource as Any
                }
            }
        }

        val routing = DynamicRoutingDataSource()
        routing.setTargetDataSources(targets)
        routing.afterPropertiesSet()
        return routing
    }

}
