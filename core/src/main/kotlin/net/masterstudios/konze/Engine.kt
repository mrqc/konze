package net.masterstudios.konze

import net.masterstudios.konze.database.DatabaseAdministrationManager
import net.masterstudios.konze.database.HikariPoolManager
import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.YamlFileReader

public data class DatabaseContext (
    private val configFilePath: String
) {
    public val configuration: ConfigurationFile
    public val databaseAdministrationManager: DatabaseAdministrationManager
    public val poolManager: HikariPoolManager
    init {
        val reader = YamlFileReader(configFilePath)
        configuration = reader.readAs<ConfigurationFile>()
        require(!configuration.konze.databaseContextId.isNullOrBlank()) {
            "databaseContextId must be filled up in configuration"
        }
        databaseAdministrationManager = DatabaseAdministrationManager(configuration)
        poolManager = HikariPoolManager(configuration, databaseAdministrationManager)
    }
}

public class Engine(private val configFilePaths: List<String>) : AutoCloseable {
    private val databaseContexts: Map<String, DatabaseContext> = emptyMap()
    init {
        for (configFilePath in configFilePaths) {
            val databaseContext: DatabaseContext = databaseContexts.getValue(configFilePath)
            databaseContexts.plus(databaseContext.configuration.konze.databaseContextId to databaseContext)
        }
    }

    override fun close() {
        for (databaseContext in databaseContexts) {
            databaseContext.value.poolManager.close()
            databaseContext.value.databaseAdministrationManager.close()
        }
    }
}
