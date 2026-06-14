package net.masterstudios.konze.database

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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

    /**
     * when there is a create or alter done on objects then it is important
     * to reassign the owner to the base role for the konze administrated users.
     * Because of a create table all privileges from the definer user are taken
     * by the trigger defined in the Driver. But now the define must change also.
     * Further the privs are set to the ones defined in the yaml file. This is done here.
     */
    override fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long) {
        if ( !(sql.lowercase().contains("create") || sql.lowercase().contains("alter"))) {
            return
        }
        databaseContexts.forEach { (key, context) ->
            context.poolManager.getAllPools().forEach { string, pool ->
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
