package net.masterstudios.konze.agent

import net.bytebuddy.asm.Advice
import net.masterstudios.konze.agent.logging.Logger
import java.lang.reflect.Method
import java.sql.Statement
import java.sql.PreparedStatement


object QueryExecutionInterceptor {
    @JvmStatic
    public val NO_MONITORING_MARKER = "@@NOMONITORING@@"
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
        @Advice.AllArguments args: Array<Any?>,
        @Advice.Origin method: Method
    ): Long {
        try {
            if (target is Statement) {
                val sql = extractSql(target, args) ?: return 0L
                if (sql.contains(NO_MONITORING_MARKER) || sql.trim().isEmpty()) {
                    return -1
                }
                val connection = target.connection
                Logger.info("${method.name} sql: $sql")
                synchronized(delegates) {
                    delegates.forEach { it.onStatementExecuteInvoke(sql, connection) }
                }
                return System.currentTimeMillis()
            }
        } catch (e: Exception) {
            Logger.error("error logging sql (enter): ${e.message}")
        }
        return -1L
    }

    @Advice.OnMethodExit(onThrowable = Throwable::class, inline = false)
    @JvmStatic
    fun exit(
        @Advice.This target: Any,
        @Advice.AllArguments args: Array<Any?>,
        @Advice.Enter startTime: Long,
        @Advice.Origin method: Method
    ) {
        if (startTime == -1L) {
            return
        }
        try {
            if (target is Statement && startTime != 0L) {
                val sql = extractSql(target, args) ?: return
                val durationMs = System.currentTimeMillis() - startTime
                val connection = target.connection
                Logger.info("${method.name} finished sql: $sql (took ${durationMs}ms)")
                synchronized(delegates) {
                    delegates.forEach { it.onStatementExecuteFinished(sql, connection, durationMs) }
                }
            }
        } catch (e: Exception) {
            Logger.error("error logging sql (exit): ${e.message}")
        }
    }
}
