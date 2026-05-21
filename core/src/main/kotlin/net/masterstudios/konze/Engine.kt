package net.masterstudios.konze

import net.masterstudios.konze.database.DatabaseAdministrationManager
import net.masterstudios.konze.database.HikariPoolManager
import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.YamlFileReader

public class Engine(private val configFilePath: String) : AutoCloseable {
    private val reader: YamlFileReader = YamlFileReader(configFilePath)
    private val configuration: ConfigurationFile = reader.readAs<ConfigurationFile>()
    
    public val poolManager: HikariPoolManager = HikariPoolManager(configuration)
    public val databaseAdministrationManager: DatabaseAdministrationManager = DatabaseAdministrationManager(configuration)

    override fun close() {
        poolManager.close()
        databaseAdministrationManager.close()
    }
}
