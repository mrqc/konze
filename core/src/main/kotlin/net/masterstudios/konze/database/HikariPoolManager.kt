package net.masterstudios.konze.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.masterstudios.konze.yaml.ConfigurationFile
import java.io.Closeable

public class HikariPoolManager(private val configuration: ConfigurationFile) : Closeable {
    private val pools: Map<String, HikariDataSource>

    init {
        pools = configuration.konze.profiles.mapValues { (profileName, profileConfig) ->
            val poolConfig = profileConfig.pool
            val config = HikariConfig().apply {
                // Use profile name as part of pool name
                poolName = "${poolConfig.poolName ?: "KonzePool"}-$profileName"
                
                dataSourceClassName = poolConfig.dataSourceClassName
                jdbcUrl = poolConfig.jdbcUrl
                username = poolConfig.username
                password = poolConfig.password
                isAutoCommit = poolConfig.autoCommit
                connectionTimeout = poolConfig.connectionTimeout
                idleTimeout = poolConfig.idleTimeout
                keepaliveTime = poolConfig.keepaliveTime
                maxLifetime = poolConfig.maxLifetime
                connectionTestQuery = poolConfig.connectionTestQuery
                minimumIdle = poolConfig.minimumIdle
                maximumPoolSize = poolConfig.maximumPoolSize
                initializationFailTimeout = poolConfig.initializationFailTimeout
                isReadOnly = poolConfig.readOnly
                connectionInitSql = poolConfig.connectionInitSql
                transactionIsolation = poolConfig.transactionIsolation
                validationTimeout = poolConfig.validationTimeout
                leakDetectionThreshold = poolConfig.leakDetectionThreshold
                schema = poolConfig.schema
            }
            HikariDataSource(config)
        }
    }

    public fun getPool(profileName: String): HikariDataSource? {
        return pools[profileName]
    }

    public fun getAllPools(): Map<String, HikariDataSource> {
        return pools
    }

    override fun close() {
        pools.values.forEach { it.close() }
    }
}
