package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.ConfigurationFile
import net.masterstudios.konze.yaml.Permission
import java.sql.Connection

public abstract class DatabaseDriver(
    protected val connection: Connection,
    protected val configuration: ConfigurationFile
) {
    public abstract fun setupDatabase()
    
    public abstract fun createRole(roleName: String)
    
    public abstract fun isUserExisting(username: String): Boolean

    public abstract fun createUser(username: String, password: String)

    public abstract fun revokeAllPermissionsOnUser(username: String, schema: String)
    
    public abstract fun setPasswordToUser(username: String, password: String)
    
    public abstract fun grantPermissionsOnUser(username: String, schema: String, permissions: List<Permission>)
    
    public abstract fun setQueryTimeoutForUser(username: String, executionTimeout: String)
}
