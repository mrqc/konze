package net.masterstudios.konze.agent

import net.bytebuddy.asm.Advice
import java.sql.Statement

object QueryExecutionInterceptor {
    @JvmField
    public var delegate: QueryExecutionDelegate? = null

    @Advice.OnMethodEnter
    @JvmStatic
    fun enter(@Advice.This target: Any): String? {
        try {
            if (target is Statement) {
                val sql = target.toString()
                val connection = target.connection
                
                delegate?.onStatementExecuteInvoke(sql, connection)
                
                println("[AGENT] executing sql: $sql")
                return sql
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql (enter): ${e.message}")
        }
        return null
    }

    @Advice.OnMethodExit(onThrowable = Throwable::class)
    @JvmStatic
    fun exit(@Advice.This target: Any, @Advice.Enter sql: String?) {
        try {
            if (target is Statement && sql != null) {
                val connection = target.connection
                delegate?.onStatementExecuteFinished(sql, connection)
                println("[AGENT] finished sql: $sql")
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql (exit): ${e.message}")
        }
    }
}
