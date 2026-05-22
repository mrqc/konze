package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.ConfigurationFile
import java.sql.Connection

public class TestDatabaseDriver(
    connection: Connection,
    configuration: ConfigurationFile
) : DatabaseDriver(connection, configuration) {

    override fun isUserExisting(username: String): Boolean = false
    override fun createUser(username: String, password: String) {}
    override fun revokeAllPermissionsOnUser(username: String, schema: String) {}
    override fun setPasswordToUser(username: String, password: String) {}
}
