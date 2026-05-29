package net.masterstudios.konze.agent

import net.bytebuddy.asm.Advice
import java.sql.Statement
import java.util.concurrent.ConcurrentHashMap

object QueryExecutionInterceptor {
    @JvmField
    public var delegate: QueryExecutionInterceptorDelegate? = null

    @JvmStatic
    private val statementTimestamps = ConcurrentHashMap<Statement, Long>()

    @Advice.OnMethodEnter(inline = false)
    @JvmStatic
    fun enter(@Advice.This target: Any): String? {
        try {
            if (target is Statement) {
                val sql = target.toString()
                val connection = target.connection
                val threadId = Thread.currentThread().threadId()
                
                statementTimestamps[target] = System.currentTimeMillis()
                
                delegate?.onStatementExecuteInvoke(sql, connection)
                
                println("[AGENT] [Thread-$threadId] executing sql: $sql")
                return sql
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql (enter): ${e.message}")
        }
        return null
    }

    @Advice.OnMethodExit(onThrowable = Throwable::class, inline = false)
    @JvmStatic
    fun exit(@Advice.This target: Any, @Advice.Enter sql: String?) {
        try {
            if (target is Statement && sql != null) {
                val threadId = Thread.currentThread().threadId()
                val startTime = statementTimestamps.remove(target)
                val durationMs = if (startTime != null) System.currentTimeMillis() - startTime else -1L
                
                val connection = target.connection
                delegate?.onStatementExecuteFinished(sql, connection, durationMs)
                println("[AGENT] [Thread-$threadId] finished sql: $sql (took ${durationMs}ms)")
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql (exit): ${e.message}")
        }
    }
}
