package net.masterstudios.konze.database

import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionInterceptorDelegate
import java.sql.Connection

public class PrivilegeAssignerInterceptorDelegate(
    public val databaseContexts: MutableMap<String, DatabaseContext>
) : QueryExecutionInterceptorDelegate {

    override fun onStatementExecuteInvoke(sql: String, connection: Connection) {
    }

    override fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long) {
        databaseContexts.forEach { key, context -> 
            context.poolManager.getPoolFromConnection(connection)?.let { pool ->
                val pool = context.poolManager.getPoolFromConnection(connection) ?: return@forEach
                pool.poolConfiguration.username ?: return@forEach
                pool.poolConfiguration.schema ?: return@forEach
                
                context.databaseAdministrationManager.driver.grantPermissionsOnUser(
                    pool.poolConfiguration.username!!,
                    pool.poolConfiguration.schema!!,
                    pool.profileConfiguration.permissions
                )
            }
        }
    }
}
