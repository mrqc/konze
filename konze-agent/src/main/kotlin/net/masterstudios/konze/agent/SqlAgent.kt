package net.masterstudios.konze.agent

import java.lang.instrument.Instrumentation
import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.asm.Advice
import net.bytebuddy.matcher.ElementMatchers

object SqlAgent {

    @JvmStatic
    fun premain(agentArgs: String?, inst: Instrumentation) {
        println("[AGENT] SQL Logging Agent successfully initialized!")

        AgentBuilder.Default()
            // Target any class implementing JDBC's Statement or PreparedStatement
            .type(ElementMatchers.hasSuperType(ElementMatchers.named("java.sql.Statement")))
            .transform { builder, _, _, _, _ ->
                // Intercept execution methods (execute, executeQuery, executeUpdate)
                builder.method(ElementMatchers.nameStartsWith("execute"))
                    .intercept(Advice.to(SqlInterceptor::class.java))
            }
            .installOn(inst)
    }
}
