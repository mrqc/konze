package net.masterstudios.konze.database

import net.masterstudios.konze.DatabaseContext
import net.masterstudios.konze.agent.QueryExecutionInterceptorDelegate
import java.sql.Connection

public class OwnershipTransferInterceptor(private val databaseContexts: MutableMap<String, DatabaseContext>) : QueryExecutionInterceptorDelegate {
    override fun onStatementExecuteInvoke(sql: String, connection: Connection) {
        // intentionally left blank
    }

    override fun onStatementExecuteFinished(sql: String, connection: Connection, durationMs: Long) {
        databaseContexts.forEach { contextKey, context -> context.databaseAdministrationManager.driver.prepareOwnershipTransfer() }
    }
}
