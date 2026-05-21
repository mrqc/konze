package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.DatabaseAdministration
import java.io.Closeable
import java.sql.Connection
import java.sql.DriverManager

public class DatabaseAdministrationManager(private val configuration: ConfigurationFile) : Closeable {
    public val administrationConfig: DatabaseAdministration? = configuration.konze.databaseAdministration
    public val connection: Connection?
    public val driver: DatabaseDriver?

    init {
        val access = administrationConfig?.access
        connection = if (access?.jdbcUrl != null) {
            DriverManager.getConnection(access.jdbcUrl, access.username, access.password)
        } else {
            null
        }
        
        driver = connection?.let { conn ->
            val driverClassName = access?.driver
            if (driverClassName != null) {
                try {
                    val clazz = Class.forName(driverClassName)
                    val constructor = clazz.getConstructor(Connection::class.java)
                    constructor.newInstance(conn) as DatabaseDriver
                } catch (e: Exception) {
                    DatabaseDriver(conn)
                }
            } else {
                DatabaseDriver(conn)
            }
        }
    }

    override fun close() {
        connection?.close()
    }
}
