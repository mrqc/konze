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

    override fun setQueryTimeoutForUser(username: String, executionTimeout: String) {
        connection.createStatement().use { statement ->
            statement.execute("alter user \"$username\" set statement_timeout = '" + executionTimeout + "'");
        }
    }
    
    override fun prepareHistorization() {
        connection.createStatement().use { statement ->
            statement.execute("""
                create table if not exists data_history (
                    id bigserial primary key,
                    table_name text not null,
                    operation text not null check (operation in ('INSERT', 'UPDATE', 'DELETE', 'insert', 'update', 'delete')),
                    old_data jsonb,
                    new_data jsonb,
                    changed_by text default current_user,
                    changed_at timestamptz default current_timestamp
                );
            """.trimIndent())

            statement.execute("""
                do $$
                begin
                    if not exists (select 1 from pg_proc where proname = 'log_table_history') then
                        create or replace function log_table_history()
                        returns trigger as ${'$'}body${'$'}
                        begin
                            if (tg_op = 'DELETE') then
                                insert into data_history (table_name, operation, old_data, new_data)
                                values (tg_table_name, tg_op, to_jsonb(old), null);
                                return old;
                            elsif (tg_op = 'UPDATE') then
                                insert into data_history (table_name, operation, old_data, new_data)
                                values (tg_table_name, tg_op, to_jsonb(old), to_jsonb(new));
                                return new;
                            elsif (tg_op = 'INSERT') then
                                insert into data_history (table_name, operation, old_data, new_data)
                                values (tg_table_name, tg_op, null, to_jsonb(new));
                                return new;
                            end if;
                            return null;
                        end;
                        ${'$'}body${'$'} language plpgsql;
                    end if;
                end $$;
            """.trimIndent())

            val schemaConfig = configuration.konze.databaseAdministration?.schema
            val operations = mutableListOf<String>()
            if (schemaConfig?.insertTrigger != false) operations.add("insert")
            if (schemaConfig?.updateTrigger != false) operations.add("update")
            if (schemaConfig?.deleteTrigger != false) operations.add("delete")

            if (operations.isNotEmpty()) {
                val operationsSql = operations.joinToString(" or ")
                val requiredOps = operations.map { it.uppercase() }.toSet()
                
                connection.createStatement().use { tableStatement ->
                    val tablesQuery = """
                        select table_name, table_schema 
                            from information_schema.tables 
                            where table_type = 'BASE TABLE' 
                                and table_schema not in ('information_schema', 'pg_catalog')
                                and table_name != 'data_history'
                    """.trimIndent()
                    
                    tableStatement.executeQuery(tablesQuery).use { resultSet ->
                        while (resultSet.next()) {
                            val tableName = resultSet.getString("table_name")
                            val tableSchema = resultSet.getString("table_schema")
                            val triggerName = "${tableName}_history_trigger"
                            
                            // Check existing operations for this trigger
                            val existingOps = mutableSetOf<String>()
                            val checkQuery = """
                                select event_manipulation 
                                from information_schema.triggers 
                                where trigger_name = ? 
                                  and event_object_table = ? 
                                  and event_object_schema = ?
                            """.trimIndent()
                            
                            connection.prepareStatement(checkQuery).use { checkStmt ->
                                checkStmt.setString(1, triggerName)
                                checkStmt.setString(2, tableName)
                                checkStmt.setString(3, tableSchema)
                                checkStmt.executeQuery().use { rs ->
                                    while (rs.next()) {
                                        existingOps.add(rs.getString("event_manipulation"))
                                    }
                                }
                            }

                            if (existingOps != requiredOps) {
                                statement.execute("drop trigger if exists \"$triggerName\" on \"$tableSchema\".\"$tableName\"")
                                statement.execute("""
                                    create trigger "$triggerName"
                                    after $operationsSql on "$tableSchema"."$tableName"
                                    for each row execute function log_table_history()
                                """.trimIndent())
                            }
                        }
                    }
                }
            }
        }
    }
}
