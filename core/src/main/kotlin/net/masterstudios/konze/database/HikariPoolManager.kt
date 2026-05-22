package net.masterstudios.konze.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.masterstudios.konze.yaml.ConfigurationFile
import java.io.Closeable

public class HikariPoolManager(
    private val configuration: ConfigurationFile,
    private val databaseAdministrationManager: DatabaseAdministrationManager
) : Closeable {
    private val pools: Map<String, HikariDataSource>
    private val passwordGenerator = PasswordGenerator()

    init {
        pools = configuration.konze.profiles.mapValues { (profileName, profileConfig) ->
            val poolConfig = profileConfig.pool
            val username = poolConfig.username ?: "konze_$profileName"
            val password = poolConfig.password ?: passwordGenerator.generate()
            val schema = poolConfig.schema ?: "public"

            databaseAdministrationManager.ensureUserExistenceAndPermissions(username, password, schema, profileConfig.permissions)

            val config = HikariConfig().apply {
                poolName = "${poolConfig.poolName ?: "KonzePool"}-$profileName"
                dataSourceClassName = poolConfig.dataSourceClassName
                jdbcUrl = poolConfig.jdbcUrl
                this.username = username
                this.password = password
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
                this.schema = schema
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
