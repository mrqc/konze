package net.masterstudios.konze.logging

import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionInterceptorDelegate
import java.sql.Connection
import java.time.LocalDateTime

public class QueryExecutionInterceptorLogger(
    public val databaseContexts: MutableMap<String, DatabaseContext>
) : Logger(), QueryExecutionInterceptorDelegate {

    private fun logToFile(connection: Connection, message: String) {
        for (databaseContext in databaseContexts.values) {
            val pool = databaseContext.poolManager.getPoolFromConnection(connection) ?: continue
            
            if (!pool.profileConfiguration.configuration.query.executionLogging) return
            
            val logPath = pool.profileConfiguration.configuration.query.executionLog
            val threadId = Thread.currentThread().threadId()
            val timestamp = LocalDateTime.now()
            
            synchronized(databaseContext) {
                java.io.File(logPath).apply {
                    parentFile?.mkdirs()
                    appendText("[$timestamp] [Thread-$threadId] $message\n")
                }
            }
        }
    }

    override fun onStatementExecuteInvoke(sql: String, connection: Connection) {
        logToFile(connection, "Invoke: $sql")
    }

    override fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long) {
        logToFile(connection, "Finished: $sql (took ${durationMs}ms)")
    }
}
