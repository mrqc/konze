package net.masterstudios.konze.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

public class HikariPoolWrapper(
    public val config: HikariConfig
) {
    public var hikariDataSource: HikariDataSource = HikariDataSource(config)

    public fun close() {
        hikariDataSource.close()
    }
}
