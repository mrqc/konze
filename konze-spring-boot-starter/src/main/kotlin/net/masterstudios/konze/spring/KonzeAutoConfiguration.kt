package net.masterstudios.konze.spring

import net.masterstudios.konze.Engine
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

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
}
