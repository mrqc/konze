package net.masterstudios.konze.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.masterstudios.konze.yaml.ProfileConfiguration

public class HikariPoolWrapper(
    public val hikariPoolConfig: HikariConfig,
    public val profileConfiguration: ProfileConfiguration
) {
    public var hikariDataSource: HikariDataSource = HikariDataSource(hikariPoolConfig)

    public fun close() {
        hikariDataSource.close()
    }
}
