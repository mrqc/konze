package net.masterstudios.konze.driver.postgres

import net.masterstudios.konze.database.DatabaseDriver
import net.masterstudios.konze.yaml.ConfigurationFile
import java.sql.Connection

public class PostgresDatabaseDriver(
    connection: Connection,
    configuration: ConfigurationFile
) : DatabaseDriver(connection, configuration) {

    override fun isUserExisting(username: String): Boolean {
        require(username.isNotBlank()) { "username must not be null or empty" }
        
        val query = "select 1 from pg_roles where rolname = ?"
        return connection.prepareStatement(query).use { statement ->
            statement.setString(1, username)
            statement.executeQuery().use { it.next() }
        }
    }

    override fun createUser(username: String, password: String) {
        require(username.isNotBlank()) { "username must not be null or empty" }
        require(password.isNotBlank()) { "password must not be null or empty" }
        
        val sql = "create user \"$username\" with password '$password';"
        connection.createStatement().use { it.execute(sql) }
    }

    override fun revokeAllPermissionsOnUser(username: String, schema: String) {
        require(username.isNotBlank()) { "username must not be null or empty" }
        require(schema.isNotBlank()) { "schema must not be null or empty" }
        
        val sql = "revoke all privileges on all tables in schema \"$schema\" from \"$username\""
        connection.createStatement().use { it.execute(sql) }
    }

    override fun setPasswordToUser(username: String, password: String) {
        require(username.isNotBlank()) { "username must not be null or empty" }
        require(password.isNotBlank()) { "password must not be null or empty" }
        
        val sql = "alter user \"$username\" with password '$password';"
        connection.createStatement().use { it.execute(sql) }
    }

    override fun grantPermissionsOnUser(username: String, permissions: List<String>) {
        require(username.isNotBlank()) { "username must not be null or empty" }
        require(permissions.isNotEmpty()) { "permissions must not be empty" }
        
        val permissionsString = permissions.joinToString(", ")
        val sql = "grant $permissionsString to \"$username\";"
        connection.createStatement().use { it.execute(sql) }
    }
}
