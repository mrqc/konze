package net.masterstudios.konze.exampleapp

import net.masterstudios.konze.Engine
import java.io.File
import java.sql.SQLException

public class ExampleApplication {
    public companion object {
        @JvmStatic
        public fun main(args: Array<String>) {
            // Locate the example-spec.yaml. 
            val configPath = "src/main/resources/example-spec.yaml"
            
            if (!File(configPath).exists()) {
                println("Error: Configuration file not found at $configPath")
                return
            }

            println("Starting Konze Example Application...")
            
            Engine(configPath).use { engine ->
                println("Engine initialized. Connection pools created.")
                
                // 1. Setup table with full access
                val fullAccessProfile = "full-access-profile"
                val fullAccessDs = engine.poolManager.getPool(fullAccessProfile)
                
                if (fullAccessDs != null) {
                    fullAccessDs.connection.use { connection ->
                        connection.createStatement().use { statement ->
                            println("Cleanup and creating example table 'agents' with full-access-profile...")
                            statement.execute("DROP TABLE IF EXISTS agents")
                            statement.execute("""
                                CREATE TABLE agents (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    role VARCHAR(255)
                                )
                            """.trimIndent())
                            
                            println("Inserting sample data with full-access-profile...")
                            statement.execute("INSERT INTO agents (name, role) VALUES ('Master-Agent', 'Admin')")
                        }
                    }
                }

                // 2. Try to insert with read-only profile
                val readOnlyProfile = "read-only-profile"
                val readOnlyDs = engine.poolManager.getPool(readOnlyProfile)
                
                if (readOnlyDs != null) {
                    println("Attempting to insert into 'agents' with read-only-profile (expecting failure)...")
                    try {
                        readOnlyDs.connection.use { connection ->
                            connection.createStatement().use { statement ->
                                statement.execute("INSERT INTO agents (name, role) VALUES ('Unauthorized', 'Hacker')")
                            }
                        }
                        println("ERROR: Insert unexpectedly succeeded with read-only-profile!")
                    } catch (e: SQLException) {
                        println("SUCCESS: Insert failed as expected. Error: ${e.message}")
                    }
                } else {
                    println("Error: Could not find connection pool for profile '$readOnlyProfile'")
                }

                // 3. Verify read access works
                if (readOnlyDs != null) {
                    println("Verifying read access with read-only-profile...")
                    try {
                        readOnlyDs.connection.use { connection ->
                            connection.createStatement().use { statement ->
                                val rs = statement.executeQuery("SELECT count(*) FROM agents")
                                if (rs.next()) {
                                    println("Successfully read count: ${rs.getInt(1)}")
                                }
                            }
                        }
                    } catch (e: SQLException) {
                        println("Error: Read access failed. ${e.message}")
                    }
                }
            }
            
            println("Example Application finished.")
        }
    }
}
