package net.masterstudios.net.masterstudios.konze.cli

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.parameters.options.option
import net.masterstudios.konze.Engine
import java.io.File
import java.sql.SQLException
import kotlin.use


public class CLI : CliktCommand() {
    private val query: String? by option("-q", "--query", help = "The SQL query to run")
    private val configFile: String? by option("-c", "--config", help = "The config file to run")

    override fun run() {
        if (query == null) {
            println("Error: No query specified!")
            return
        }
        
        val config = configFile
        if (config == null) {
            println("Error: No config file specified!")
            return
        }

        if (!File(config).exists()) {
            println("Error: Configuration file not found at $config")
            return
        }

        val guard = GuardFile(".guard")
        if (!guard.exists()) {
            println("Error: .guard file not found in the current directory!")
            return
        }

        val profile = guard["konze_profile"]
        if (profile.isNullOrBlank()) {
            println("Error: konze_profile is not set in .guard file!")
            return
        }
        
        Engine(listOf(config)).use { engine ->
            println("Engine initialized with profile '$profile'. Connection pools created.")
            val profileDs = engine.getDatabaseContext("sample-context")!!.poolManager.getPool(profile)?.hikariDataSource

            if (profileDs != null) {
                println("Attempting to execute '$query' with profile $profile...")
                try {
                    profileDs.connection.use { connection ->
                        connection.createStatement().use { statement ->
                            statement.execute(query)
                        }
                    }
                } catch (e: SQLException) {
                    println("Error for query '$query': ${e.message}")
                }

            } else {
                println("Error: Could not find connection pool for profile '$profile'")
            }
        }
    }

    public companion object {
        @JvmStatic
        public fun main(args: Array<String>) {
            CLI().main(args)
        }
    }
}
