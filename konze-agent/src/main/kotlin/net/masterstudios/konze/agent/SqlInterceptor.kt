package net.masterstudios.konze.agent

import net.bytebuddy.asm.Advice
import java.sql.Statement

object SqlInterceptor {
    @Advice.OnMethodEnter
    @JvmStatic
    fun enter(@Advice.This target: Any, @Advice.Origin origin: String) {
        try {
            val sql = if (target is Statement) {
                // Many JDBC drivers override toString() to provide the SQL
                target.toString()
            } else {
                "Unknown SQL"
            }
            println("[AGENT] executing sql: $sql")
        } catch (e: Exception) {
            println("[AGENT] error logging sql: ${e.message}")
        }
    }
}
