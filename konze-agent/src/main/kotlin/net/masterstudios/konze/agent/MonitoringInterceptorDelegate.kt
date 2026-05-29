package net.masterstudios.konze.agent

import java.sql.Connection

interface MonitoringInterceptorDelegate {
    public fun onStatementExecuteInvoke(sql: String, connection: Connection)
    public fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long)
}
