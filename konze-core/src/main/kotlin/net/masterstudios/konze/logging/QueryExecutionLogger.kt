package net.masterstudios.konze.logging

import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionDelegate
import java.sql.Connection

public class QueryExecutionLogger(
    public val databaseContexts: MutableMap<String, DatabaseContext>
) : Logger(), QueryExecutionDelegate {
    override fun onStatementExecuteInvoke(sql: String, connection: Connection) {
        for (databaseContext in databaseContexts.values) {
            val pool = databaseContext.poolManager.getPoolFromConnection(connection) ?: continue
            
            if (!pool.profileConfiguration.configuration.query.executionLogging) return
            
            val logPath = pool.profileConfiguration.configuration.query.executionLog
            synchronized(databaseContext) {
                java.io.File(logPath).apply {
                    parentFile?.mkdirs()
                    appendText("$sql\n")
                }
            }
            return
        }
    }

    override fun onStatementExecuteFinished(sql: String, connection: Connection) {
        // Implementation for when the statement is finished
    }
}
