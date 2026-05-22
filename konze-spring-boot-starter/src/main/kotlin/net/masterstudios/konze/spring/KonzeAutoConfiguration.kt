package net.masterstudios.konze.spring

import net.masterstudios.konze.Engine
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
public open class KonzeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public open fun konzeEngine(): Engine {
        // This is just a placeholder, in a real scenario we'd need a config path
        return Engine("example-app/src/main/resources/example-spec.yaml")
    }
}
