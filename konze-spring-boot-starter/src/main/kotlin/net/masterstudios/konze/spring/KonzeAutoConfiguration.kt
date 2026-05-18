package net.masterstudios.konze.spring

class KonzeAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean // Allows users to override this bean with their own custom setup if they want
    open fun konzeEngine(): KonzeEngine {
        // Instantiate your library engine here
        return KonzeEngine()
    }
}
