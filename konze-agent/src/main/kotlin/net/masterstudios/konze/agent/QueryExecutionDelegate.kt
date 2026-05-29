package net.masterstudios.konze.agent

import java.sql.Connection

public interface QueryExecutionDelegate {
    public fun onStatementExecuteInvoke(sql: String, connection: Connection)
}
