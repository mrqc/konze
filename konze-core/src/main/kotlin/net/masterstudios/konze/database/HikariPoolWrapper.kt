package net.masterstudios.konze.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.masterstudios.konze.schemadiscovery.SchemaDiscovery
import net.masterstudios.konze.yaml.PoolConfiguration
import net.masterstudios.konze.yaml.ProfileConfiguration

public class HikariPoolWrapper(
    public val hikariPoolConfig: HikariConfig,
    public val profileConfiguration: ProfileConfiguration,
    public val poolConfiguration: PoolConfiguration,
    public val schemaDiscovery: SchemaDiscovery? = null,
) {
    public var hikariDataSource: HikariDataSource = HikariDataSource(hikariPoolConfig)

    public fun close() {
        hikariDataSource.close()
    }
}
