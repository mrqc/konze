package net.masterstudios.konze.yaml

import com.fasterxml.jackson.annotation.JsonProperty

public data class ConfigurationFile(
    public val konze: Konze = Konze()
)

public data class Konze(
    public val profiles: Map<String, ProfileConfiguration> = emptyMap(),
    @field:JsonProperty("database-administration")
    public val databaseAdministration: DatabaseAdministration? = null
)

public data class ProfileConfiguration(
    public val permissions: List<String> = emptyList(),
    public val configuration: ProfileSpecificConfiguration = ProfileSpecificConfiguration(),
    @field:JsonProperty("schema-discovery-endpoint")
    public val schemaDiscoveryEndpoint: SchemaDiscoveryEndpointConfiguration = SchemaDiscoveryEndpointConfiguration(),
    public val pool: PoolConfiguration = PoolConfiguration()
)

public data class ProfileSpecificConfiguration(
    public val query: QueryConfiguration = QueryConfiguration(),
    public val audit: AuditConfiguration = AuditConfiguration(),
    public val monitoring: MonitoringConfiguration = MonitoringConfiguration()
)

public data class QueryConfiguration(
    @field:JsonProperty("row-limit")
    public val rowLimit: Int = 1000,
    @field:JsonProperty("execution-timeout")
    public val executionTimeout: String = "60s",
    @field:JsonProperty("execution-logging")
    public val executionLogging: Boolean = true,
    @field:JsonProperty("execution-log")
    public val executionLog: String = "./logs/execution.log"
)

public data class AuditConfiguration(
    public val log: String = "./logs/audit.log"
)

public data class MonitoringConfiguration(
    @field:JsonProperty("slow-query-threshold")
    public val slowQueryThreshold: String = "500ms",
    @field:JsonProperty("slow-query-logging")
    public val slowQueryLogging: Boolean = true,
    @field:JsonProperty("slow-query-log")
    public val slowQueryLog: String = "./logs/slow-queries.log"
)

public data class SchemaDiscoveryEndpointConfiguration(
    public val enabled: Boolean = true,
    public val endpoint: String = "/schema-discovery",
    public val authentication: Boolean = true,
    @field:JsonProperty("rate-limiting")
    public val rateLimiting: String = "100 requests per minute"
)

public data class PoolConfiguration(
    public val dataSourceClassName: String? = null,
    public val jdbcUrl: String? = null,
    public val username: String? = null,
    public val password: String? = null,
    public val autoCommit: Boolean = true,
    public val connectionTimeout: Long = 30000,
    public val idleTimeout: Long = 600000,
    public val keepaliveTime: Long = 0,
    public val maxLifetime: Long = 1800000,
    public val connectionTestQuery: String? = null,
    public val minimumIdle: Int = 10,
    public val maximumPoolSize: Int = 20,
    public val poolName: String? = null,
    public val initializationFailTimeout: Long = 1,
    public val readOnly: Boolean = false,
    public val connectionInitSql: String? = null,
    public val transactionIsolation: String? = null,
    public val validationTimeout: Long = 5000,
    public val leakDetectionThreshold: Long = 2000,
    public val schema: String? = null
)

public data class DatabaseAdministration(
    public val access: DatabaseAccess = DatabaseAccess(),
    public val schema: List<Map<String, Boolean>> = emptyList()
)

public data class DatabaseAccess(
    public val driver: String? = null,
    public val jdbcUrl: String? = null,
    public val username: String? = null,
    public val password: String? = null
)
