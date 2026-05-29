package net.masterstudios.konze.logging

import com.zaxxer.hikari.HikariDataSource
import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionDelegate
import java.sql.Connection

public class QueryExecutionLogger(
    public val databaseContexts: MutableMap<String, DatabaseContext>
) : Logger(), QueryExecutionDelegate {
    override fun onStatementExecuteInvoke(sql: String, connection: Connection) {
        databaseContexts.values.forEach { databaseContext -> 
            val hikariDataSource = databaseContext.poolManager.getPoolFromConnection(connection)
        }
    }
}
