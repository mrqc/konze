package net.masterstudios.konze.logging

import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionInterceptorDelegate
import java.io.File
import java.sql.Connection
import java.time.LocalDateTime

public class MonitoringInterceptorLogger(
    public val databaseContexts: MutableMap<String, DatabaseContext>
) : Logger(), QueryExecutionInterceptorDelegate {

    private fun logToFile(connection: Connection, message: String) {
        for (databaseContext in databaseContexts.values) {
            val pool = databaseContext.poolManager.getPoolFromConnection(connection) ?: continue
            if (!pool.profileConfiguration.configuration.monitoring.slowQueryLogging) return
            val logPath = pool.profileConfiguration.configuration.monitoring.slowQueryLog
            val threadId = Thread.currentThread().threadId()
            val timestamp = LocalDateTime.now()
            
            synchronized(databaseContext) {
                File(logPath).apply {
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
        for (databaseContext in databaseContexts.values) {
            val pool = databaseContext.poolManager.getPoolFromConnection(connection) ?: continue
            if (!pool.profileConfiguration.configuration.monitoring.slowQueryLogging) return
            val threshold = Integer.valueOf(pool.profileConfiguration.configuration.monitoring.slowQueryThreshold)
            if (durationMs >= threshold) {
                val logPath = pool.profileConfiguration.configuration.monitoring.slowQueryLog
                val threadId = Thread.currentThread().threadId()
                val timestamp = LocalDateTime.now()

                synchronized(databaseContext) {
                    File(logPath).apply {
                        parentFile?.mkdirs()
                        appendText("[$timestamp] [Thread-$threadId] Finished reaching threshold (${threshold}ms): $sql (took ${durationMs}ms)\n")
                    }
                }
            }
        }
    }
}
