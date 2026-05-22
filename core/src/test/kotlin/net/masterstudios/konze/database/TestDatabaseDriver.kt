package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.Permission
import java.sql.Connection

public class TestDatabaseDriver(
    connection: Connection,
    configuration: ConfigurationFile
) : DatabaseDriver(connection, configuration) {

    override fun setupDatabase() {}

    override fun createRole(roleName: String) {}

    override fun isUserExisting(username: String): Boolean = false
    override fun createUser(username: String, password: String) {}
    override fun revokeAllPermissionsOnUser(username: String, schema: String) {}
    override fun setPasswordToUser(username: String, password: String) {}
    override fun grantPermissionsOnUser(username: String, schema: String, permissions: List<Permission>) {}
}
