package net.masterstudios.konze.spring
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource

class DynamicRoutingDataSource : AbstractRoutingDataSource() {

    override fun determineCurrentLookupKey(): Any? = DataSourceContextHolder.getDataSourceType()

}
