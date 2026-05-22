package net.masterstudios.konze.driver.postgres

import net.masterstudios.konze.database.DatabaseDriver
import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.Permission
import java.sql.Connection

public class PostgresDatabaseDriver(
    connection: Connection,
    configuration: ConfigurationFile
) : DatabaseDriver(connection, configuration) {
    
    override fun setupDatabase() {
        if (!isUserExisting("konze-users")) {
            createRole("konze-users")
        }
    }

    override fun createRole(roleName: String) {
        require(roleName.isNotBlank()) { "roleName must not be null or empty" }
        val sql = "create role \"$roleName\" nologin;"
        connection.createStatement().use { it.execute(sql) }
    }

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
        
        val sql = "create user \"$username\" with password '$password' in role \"konze-users\";"
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

    override fun grantPermissionsOnUser(username: String, schema: String, permissions: List<Permission>) {
        require(username.isNotBlank()) { "username must not be null or empty" }
        
        connection.createStatement().use { statement ->
            // 0. Ensure user is part of the shared role
            statement.execute("grant \"konze-users\" to \"$username\"")

            if (permissions.isEmpty()) return

            // 1. Grant usage on schema
            statement.execute("grant usage on schema \"$schema\" to \"$username\"")

            if (permissions.contains(Permission.ALL_PRIVILEGES)) {
                statement.execute("grant all privileges on all tables in schema \"$schema\" to \"$username\"")
                statement.execute("grant all privileges on all sequences in schema \"$schema\" to \"$username\"")
                statement.execute("alter default privileges in schema \"$schema\" grant all privileges on tables to \"$username\"")
                statement.execute("alter default privileges for role \"$username\" in schema \"$schema\" grant all on tables to \"konze-users\"")
            } else {
                // 2. Map to valid table privileges
                val tablePrivileges = setOf(
                    Permission.SELECT, Permission.INSERT, Permission.UPDATE, 
                    Permission.DELETE, Permission.TRUNCATE, Permission.REFERENCES, Permission.TRIGGER
                )
                
                val privsToGrant = permissions.intersect(tablePrivileges)
                if (privsToGrant.isNotEmpty()) {
                    val privsString = privsToGrant.joinToString(", ") { it.name.lowercase() }
                    
                    // Grant on current tables
                    statement.execute("grant $privsString on all tables in schema \"$schema\" to \"$username\"")
                    
                    // Grant on sequences if it's an insert/update
                    if (permissions.contains(Permission.INSERT) || permissions.contains(Permission.UPDATE)) {
                        statement.execute("grant usage, select on all sequences in schema \"$schema\" to \"$username\"")
                    }

                    // Grant on future tables
                    statement.execute("alter default privileges in schema \"$schema\" grant $privsString on tables to \"$username\"")
                    
                    // Transfer ownership concept: Ensure 'konze-users' can manage objects
                    statement.execute("alter default privileges for role \"$username\" in schema \"$schema\" grant all on tables to \"konze-users\"")
                }

                // 3. Handle schema-level privileges
                if (permissions.contains(Permission.CREATE)) {
                    statement.execute("grant create on schema \"$schema\" to \"$username\"")
                }
            }
        }
    }
}
