package net.masterstudios.konze.agent

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.bytebuddy.asm.Advice
import java.sql.Statement
import java.sql.PreparedStatement


object QueryExecutionInterceptor {
    private val delegates: MutableList<QueryExecutionInterceptorDelegate> = ArrayList()

    @JvmStatic
    fun addDelegate(delegate: QueryExecutionInterceptorDelegate) {
        synchronized(delegates) {
            delegates.add(delegate)
        }
    }

    @JvmStatic
    fun removeDelegate(delegate: QueryExecutionInterceptorDelegate) {
        synchronized(delegates) {
            delegates.remove(delegate)
        }
    }

    private fun extractSql(target: Statement, args: Array<Any?>): String? {
        val sql: String? = if (args.isNotEmpty() && args[0] is String) {
            args[0] as String
        } else if (target is PreparedStatement) {
            target.toString()
        } else {
            null
        }

        return sql
    }

    @Advice.OnMethodEnter(inline = false)
    @JvmStatic
    fun enter(
        @Advice.This target: Any,
        @Advice.AllArguments args: Array<Any?>
    ): Long {
        try {
            if (target is Statement) {
                val sql = extractSql(target, args) ?: return 0L
                val connection = target.connection
                val threadId = Thread.currentThread().threadId()
                synchronized(delegates) {
                    delegates.forEach { it.onStatementExecuteInvoke(sql, connection) }
                }
                println("[AGENT] [Thread-$threadId] executing sql: $sql")
                return System.currentTimeMillis()
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql (enter): ${e.message}")
        }
        return 0L
    }

    @Advice.OnMethodExit(onThrowable = Throwable::class, inline = false)
    @JvmStatic
    fun exit(
        @Advice.This target: Any,
        @Advice.AllArguments args: Array<Any?>,
        @Advice.Enter startTime: Long
    ) {
        try {
            if (target is Statement && startTime != 0L) {
                val sql = extractSql(target, args) ?: return
                val durationMs = System.currentTimeMillis() - startTime
                val connection = target.connection
                val threadId = Thread.currentThread().threadId()
                synchronized(delegates) {
                    delegates.forEach { it.onStatementExecuteFinished(sql, connection, durationMs) }
                }
                println("[AGENT] [Thread-$threadId] finished sql: $sql (took ${durationMs}ms)")
            }
        } catch (e: Exception) {
            println("[AGENT] error logging sql (exit): ${e.message}")
        }
    }
}
