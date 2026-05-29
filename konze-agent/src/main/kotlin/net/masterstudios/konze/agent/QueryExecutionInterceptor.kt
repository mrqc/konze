package net.masterstudios.konze.agent

import net.bytebuddy.asm.Advice
import java.sql.Statement

object QueryExecutionInterceptor {
    @JvmField
    public var delegate: QueryExecutionDelegate? = null

    @Advice.OnMethodEnter
    @JvmStatic
    fun enter(@Advice.This target: Any, @Advice.Origin origin: String) {
        try {
            if (target is Statement) {
                val sql = target.toString()
                val connection = target.connection
                
                delegate?.onStatementExecuteInvoke(sql, connection)
                
                println("[AGENT] executing sql: $sql")
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql: ${e.message}")
        }
    }
}
