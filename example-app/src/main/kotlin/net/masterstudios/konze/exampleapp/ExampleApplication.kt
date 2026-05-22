package net.masterstudios.konze.exampleapp

import net.masterstudios.konze.Engine
import java.io.File
import java.nio.file.Paths

class ExampleApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Locate the example-spec.yaml. 
            val configPath = "src/main/resources/example-spec.yaml"
            
            if (!File(configPath).exists()) {
                println("Error: Configuration file not found at $configPath")
                return
            }

            println("Starting Konze Example Application...")
            
            Engine(configPath).use { engine ->
                println("Engine initialized. Connection pools created.")
                
                val profileName = "example-profile"
                val dataSource = engine.poolManager.getPool(profileName)
                
                if (dataSource != null) {
                    println("Connected to pool for profile: $profileName")
                    
                    dataSource.connection.use { connection ->
                        connection.createStatement().use { statement ->
                            println("Creating example table 'agents'...")
                            statement.execute("DROP TABLE IF EXISTS agents")
                            statement.execute("""
                                CREATE TABLE agents (
                                    id SERIAL PRIMARY KEY,
                                    name VARCHAR(255) NOT NULL,
                                    role VARCHAR(255)
                                )
                            """.trimIndent())
                            
                            println("Inserting sample data...")
                            statement.execute("INSERT INTO agents (name, role) VALUES ('Gemini', 'Assistant')")
                            statement.execute("INSERT INTO agents (name, role) VALUES ('Konze-Bot', 'DB-Manager')")
                            
                            val resultSet = statement.executeQuery("SELECT * FROM agents")
                            println("Retrieving data from 'agents' table:")
                            while (resultSet.next()) {
                                println(" - ID: ${resultSet.getInt("id")}, Name: ${resultSet.getString("name")}, Role: ${resultSet.getString("role")}")
                            }
                        }
                    }
                    println("Example table created and verified successfully.")
                } else {
                    println("Error: Could not find connection pool for profile '$profileName'")
                }
            }
            
            println("Example Application finished. Pools closed.")
        }
    }
}
