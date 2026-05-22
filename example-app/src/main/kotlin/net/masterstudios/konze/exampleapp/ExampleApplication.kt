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
                            // Administrative cleanup
                            println("Cleaning up existing table 'agents' with full-access-profile...")
                            statement.execute("drop table if exists agents")

                            println("Creating example table 'agents' with full-access-profile...")
                            statement.execute("""
                                create table agents (
                                    id serial primary key,
                                    name varchar(255) not null,
                                    role varchar(255)
                                )
                            """.trimIndent())
                            
                            println("Inserting sample data with full-access-profile...")
                            statement.execute("insert into agents (name, role) values ('master-agent', 'admin')")
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
                                statement.execute("insert into agents (name, role) values ('unauthorized', 'hacker')")
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
                                val resultSet = statement.executeQuery("select count(*) from agents")
                                if (resultSet.next()) {
                                    println("Successfully read count: ${resultSet.getInt(1)}")
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
