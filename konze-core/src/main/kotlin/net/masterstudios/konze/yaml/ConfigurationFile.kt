package net.masterstudios.konze.yaml

import com.fasterxml.jackson.annotation.JsonProperty

public data class ConfigurationFile(
    public val konze: Konze = Konze()
)

public data class Konze(
    public val databaseContextId: String? = null,
    public val profiles: Map<String, ProfileConfiguration> = emptyMap(),
    public val databaseAdministration: DatabaseAdministration? = null
)

public data class ProfileConfiguration(
    public val permissions: List<Permission> = emptyList(),
    public val configuration: ProfileSpecificConfiguration = ProfileSpecificConfiguration(),
    public val schemaDiscoveryEndpoint: SchemaDiscoveryEndpointConfiguration = SchemaDiscoveryEndpointConfiguration(),
    public val pool: PoolConfiguration = PoolConfiguration()
)

public enum class Permission {
    @JsonProperty("select")
    SELECT,
    @JsonProperty("insert")
    INSERT,
    @JsonProperty("update")
    UPDATE,
    @JsonProperty("delete")
    DELETE,
    @JsonProperty("truncate")
    TRUNCATE,
    @JsonProperty("references")
    REFERENCES,
    @JsonProperty("trigger")
    TRIGGER,
    @JsonProperty("maintain")
    MAINTAIN,
    @JsonProperty("usage")
    USAGE,
    @JsonProperty("create")
    CREATE,
    @JsonProperty("connect")
    CONNECT,
    @JsonProperty("temporary")
    TEMPORARY,
    @JsonProperty("execute")
    EXECUTE,
    @JsonProperty("all privileges")
    ALL_PRIVILEGES
}

public data class ProfileSpecificConfiguration(
    public val query: QueryConfiguration = QueryConfiguration(),
    public val audit: AuditConfiguration = AuditConfiguration(),
    public val monitoring: MonitoringConfiguration = MonitoringConfiguration()
)

public data class QueryConfiguration(
    public val executionTimeout: String = "60s",
    public val executionLogging: Boolean = true,
    public val executionLog: String = "./logs/execution.log"
)

public data class AuditConfiguration(
    public val log: String = "./logs/audit.log"
)

public data class MonitoringConfiguration(
    public val slowQueryThreshold: String = "500ms",
    public val slowQueryLogging: Boolean = true,
    public val slowQueryLog: String = "./logs/slow-queries.log"
)

public data class SchemaDiscoveryEndpointConfiguration(
    public val enabled: Boolean = true,
    public val endpoint: String = "/schema-discovery",
    public val authentication: Boolean = true,
    public val rateLimiting: String = "100 requests per minute"
)

public open class PoolConfiguration(
    public open val dataSourceClassName: String? = null,
    public open val jdbcUrl: String? = null,
    public open val username: String? = null,
    public open val password: String? = null,
    public open val autoCommit: Boolean = true,
    public open val connectionTimeout: Long = 30000,
    public open val idleTimeout: Long = 600000,
    public open val keepaliveTime: Long = 0,
    public open val maxLifetime: Long = 1800000,
    public open val connectionTestQuery: String? = null,
    public open val minimumIdle: Int = 10,
    public open val maximumPoolSize: Int = 20,
    public val poolName: String? = null,
    public open val initializationFailTimeout: Long = 1,
    public open val readOnly: Boolean = false,
    public open val connectionInitSql: String? = null,
    public open val transactionIsolation: String? = null,
    public open val validationTimeout: Long = 5000,
    public open val leakDetectionThreshold: Long = 2000,
    public open val schema: String? = null
)

public data class DatabaseAdministration(
    public val access: DatabaseAccess = DatabaseAccess(),
    public val schema: SchemaConfiguration = SchemaConfiguration()
)

public data class SchemaConfiguration(
    public val updateTrigger: Boolean = true,
    public val deleteTrigger: Boolean = true,
    public val insertTrigger: Boolean = true
)

public data class DatabaseAccess(
    public val driver: String? = null,
    public val jdbcUrl: String? = null,
    public val username: String? = null,
    public val password: String? = null
)
