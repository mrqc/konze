package net.masterstudios.konze.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.masterstudios.konze.schemadiscovery.SchemaDiscovery
import net.masterstudios.konze.yaml.ConfigurationFile
import java.io.Closeable
import java.sql.Connection


public class HikariPoolsManager(
    private val configuration: ConfigurationFile,
    private val databaseAdministrationManager: DatabaseAdministrationManager
) : Closeable {
    private val pools: Map<String, HikariPoolWrapper>
    private val passwordGenerator = PasswordGenerator()

    init {
        pools = configuration.konze.profiles.mapValues { (profileName, profileConfig) ->
            val poolConfig = profileConfig.pool
            val username = poolConfig.username ?: "konze_$profileName"
            val password = poolConfig.password ?: passwordGenerator.generate()
            val schema = poolConfig.schema ?: "public"

            databaseAdministrationManager.ensureUserExistenceAndPermissions(
                username, 
                password, 
                schema, 
                profileConfig.permissions,
                profileConfig.configuration)

            val hikariConfig = HikariConfig().apply {
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

            val schemaDiscoveryClassName = configuration.konze.databaseAdministration?.access?.schemaDiscovery
            val schemaDiscovery = if (schemaDiscoveryClassName != null) {
                try {
                    val clazz = Class.forName(schemaDiscoveryClassName)
                    val constructor = clazz.getConstructor(String::class.java, String::class.java, String::class.java)
                    constructor.newInstance(username, password, poolConfig.jdbcUrl) as SchemaDiscovery
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }

            HikariPoolWrapper(hikariConfig, profileConfig, poolConfig, schemaDiscovery)
        }
    }
    
    public fun getPoolBySchemaDiscoveryPath(path: String): HikariPoolWrapper? {
        return pools.values.find { poolWrapper -> 
            poolWrapper.profileConfiguration.schemaDiscoveryEndpoint.endpoint == path
        }
    }

    public fun getPool(profileName: String): HikariPoolWrapper? {
        return pools[profileName]
    }

    public fun getAllPools(): Map<String, HikariPoolWrapper> {
        return pools
    }
    
    public fun getPoolFromConnection(connection: Connection): HikariPoolWrapper? {
        try {
            // HikariProxyConnection is a subclass of ProxyConnection which holds the poolEntry
            val proxyConnectionClass = Class.forName("com.zaxxer.hikari.pool.ProxyConnection")
            if (!proxyConnectionClass.isInstance(connection)) return null

            val poolEntryField = proxyConnectionClass.getDeclaredField("poolEntry")
            poolEntryField.isAccessible = true
            val poolEntry = poolEntryField.get(connection) ?: return null

            val poolEntryClass = Class.forName("com.zaxxer.hikari.pool.PoolEntry")
            val hikariPoolField = poolEntryClass.getDeclaredField("hikariPool")
            hikariPoolField.isAccessible = true
            val hikariPool = hikariPoolField.get(poolEntry) ?: return null

            // 'config' is actually in PoolBase, which HikariPool extends
            val poolBaseClass = Class.forName("com.zaxxer.hikari.pool.PoolBase")
            val configField = poolBaseClass.getDeclaredField("config")
            configField.isAccessible = true
            val config = configField.get(hikariPool)

            return if (config is HikariDataSource) {
                // Return the instance if it's one of our managed pools
                pools.values.find { it.hikariDataSource === config }
            } else {
                null
            }
        } catch (e: Exception) {
            return null
        }
    }

    override fun close() {
        pools.values.forEach { it.close() }
    }
}
