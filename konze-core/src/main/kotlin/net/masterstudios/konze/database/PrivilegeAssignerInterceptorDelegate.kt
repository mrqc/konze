package net.masterstudios.konze.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionInterceptorDelegate
import java.sql.Connection

public class PrivilegeAssignerInterceptorDelegate(
    public val databaseContexts: MutableMap<String, DatabaseContext>
) : QueryExecutionInterceptorDelegate {

    public val backgroundScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onStatementExecuteInvoke(sql: String, connection: Connection) {
        // Intentionally left blank
    }

    override fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long) {
        if ( !(sql.lowercase().contains("create") || sql.lowercase().contains("alter"))) {
            return
        }
        databaseContexts.forEach { (key, context) ->
            context.poolManager.getPoolFromConnection(connection)?.let { pool ->
                val username = pool.hikariPoolConfig.username ?: return@forEach
                val schema = pool.poolConfiguration.schema ?: "public"
                runBlocking {
                    backgroundScope.launch {
                        context.databaseAdministrationManager.driver.grantPermissionsOnUser(
                            username,
                            schema,
                            pool.profileConfiguration.permissions
                        )
                    }
                }
            }
        }
    }
}
