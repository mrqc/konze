package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.DatabaseAdministration
import net.masterstudios.konze.yaml.Permission
import net.masterstudios.konze.yaml.ProfileSpecificConfiguration
import java.io.Closeable
import java.sql.Connection
import java.sql.DriverManager

public class DatabaseAdministrationManager(private val configuration: ConfigurationFile) : Closeable {
    public val administrationConfig: DatabaseAdministration = configuration.konze.databaseAdministration
        ?: throw IllegalStateException("databaseAdministration section is missing in configuration")
    
    public val connection: Connection
    public val driver: DatabaseDriver

    init {
        val access = administrationConfig.access
        val jdbcUrl = access.jdbcUrl
            ?: throw IllegalStateException("databaseAdministration.access.jdbcUrl is missing")
            
        connection = DriverManager.getConnection(jdbcUrl, access.username, access.password)
        
        val driverClassName = access.driver
            ?: throw IllegalStateException("databaseAdministration.access.driver is missing")
            
        try {
            val clazz = Class.forName(driverClassName)
            val constructor = clazz.getConstructor(Connection::class.java, ConfigurationFile::class.java)
            driver = constructor.newInstance(connection, configuration) as DatabaseDriver
            driver.setupDatabase()
        } catch (e: Exception) {
            throw RuntimeException("Failed to instantiate database driver: $driverClassName", e)
        }
    }

    override fun close() {
        connection.close()
    }

    public fun ensureUserExistenceAndPermissions(username: String, password: String, schema: String, permissions: List<Permission>, configuration: ProfileSpecificConfiguration) {
        if (driver.isUserExisting(username)) {
            driver.revokeAllPermissionsOnUser(username, schema)
            driver.setPasswordToUser(username, password)
        } else {
            driver.createUser(username, password)
        }
        driver.grantPermissionsOnUser(username, schema, permissions)
        driver.setQueryTimeoutForUser(username, configuration.query.executionTimeout)
    }
}
