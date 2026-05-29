package net.masterstudios.konze.agent

import java.sql.Connection

public interface QueryExecutionInterceptorDelegate {
    public fun onStatementExecuteInvoke(sql: String, connection: Connection)
    public fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long)
}
