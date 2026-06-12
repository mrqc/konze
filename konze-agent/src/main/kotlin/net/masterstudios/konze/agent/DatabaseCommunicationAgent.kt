package net.masterstudios.konze.agent

import java.lang.instrument.Instrumentation
import net.bytebuddy.agent.builder.AgentBuilder
import net.bytebuddy.asm.Advice
import net.bytebuddy.matcher.ElementMatchers

public class DatabaseCommunicationAgent private constructor() {

    public fun setQueryExecutionInterceptorDelegate(delegate: QueryExecutionInterceptorDelegate) {
        QueryExecutionInterceptor.delegate = delegate
    }
    
    companion object {
        @JvmStatic
        public val instance: DatabaseCommunicationAgent = DatabaseCommunicationAgent()

        @JvmStatic
        fun premain(agentArgs: String?, inst: Instrumentation) {
            println("[AGENT] SQL Logging Agent successfully initialized!")

            AgentBuilder.Default()
                // Target any class implementing JDBC's Statement or PreparedStatement
                .type(ElementMatchers.hasSuperType(ElementMatchers.named("java.sql.Statement")))
                .transform { builder, _, _, _, _ ->
                    // Intercept execution methods (execute, executeQuery, executeUpdate)
                    builder.method(ElementMatchers.nameStartsWith("execute"))
                        .intercept(Advice.to(QueryExecutionInterceptor::class.java))
                }
                .installOn(inst)
        }
    }
}
