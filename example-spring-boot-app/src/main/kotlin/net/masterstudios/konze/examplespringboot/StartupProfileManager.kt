package net.masterstudios.konze.examplespringboot

import net.masterstudios.konze.spring.DataSourceContextHolder.clearDataSourceType
import net.masterstudios.konze.spring.DataSourceContextHolder.setDataSourceType
import org.springframework.beans.factory.config.BeanFactoryPostProcessor
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component

/**
 * Ensures a profile is active during the Spring context initialization phase
 * so that Hibernate's ddl-auto can connect and create entities.
 */
@Component
class StartupProfileManager : BeanFactoryPostProcessor, ApplicationListener<ContextRefreshedEvent> {

    override fun postProcessBeanFactory(beanFactory: ConfigurableListableBeanFactory) {
        println("StartupProfileManager: Setting full-access-profile for context initialization...")
        setDataSourceType("full-access-profile")
    }

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        println("StartupProfileManager: Context refreshed. Clearing initialization profile.")
        clearDataSourceType()
    }
}
