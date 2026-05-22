package net.masterstudios.konze

import net.masterstudios.konze.database.TestDatabaseDriver
import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EngineTest {

    @Test
    fun `should initialize engine and pool manager from yaml file`() {
        val yamlContent = """
            konze:
              profiles:
                test-profile:
                  permissions:
                    - select
                  pool:
                    jdbcUrl: jdbc:h2:mem:engine_test;DB_CLOSE_DELAY=-1
                    username: sa
                    password: ""
                    initializationFailTimeout: -1
              databaseAdministration:
                access:
                  driver: net.masterstudios.konze.database.TestDatabaseDriver
                  jdbcUrl: jdbc:h2:mem:engine_test;DB_CLOSE_DELAY=-1
                  username: sa
                  password: ""
        """.trimIndent()

        val tempFile = File.createTempFile("engine-test", ".yaml")
        tempFile.writeText(yamlContent)

        try {
            Engine(tempFile.absolutePath).use { engine ->
                val pool = engine.poolManager.getPool("test-profile")
                assertNotNull(pool)
                assertTrue(pool.poolName.contains("test-profile"))
                assertNotNull(engine.databaseAdministrationManager)
            }
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `should initialize database administration manager with driver`() {
        val yamlContent = """
            konze:
              profiles:
                test-profile:
                  pool:
                    jdbcUrl: jdbc:h2:mem:admin_test;DB_CLOSE_DELAY=-1
                    username: sa
                    password: ""
                    initializationFailTimeout: -1
              databaseAdministration:
                access:
                  driver: net.masterstudios.konze.database.TestDatabaseDriver
                  jdbcUrl: jdbc:h2:mem:admin_test;DB_CLOSE_DELAY=-1
                  username: sa
                  password: ""
        """.trimIndent()

        val tempFile = File.createTempFile("admin-test", ".yaml")
        tempFile.writeText(yamlContent)

        try {
            Engine(tempFile.absolutePath).use { engine ->
                val adminManager = engine.databaseAdministrationManager
                assertNotNull(adminManager.connection)
                assertNotNull(adminManager.driver)
            }
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `should dynamically load specific database driver`() {
        val yamlContent = """
            konze:
              profiles:
                test-profile:
                  pool:
                    jdbcUrl: jdbc:h2:mem:dynamic_test;DB_CLOSE_DELAY=-1
                    username: sa
                    password: ""
                    initializationFailTimeout: -1
              databaseAdministration:
                access:
                  driver: net.masterstudios.konze.database.TestDatabaseDriver
                  jdbcUrl: jdbc:h2:mem:dynamic_test;DB_CLOSE_DELAY=-1
                  username: sa
                  password: ""
        """.trimIndent()

        val tempFile = File.createTempFile("dynamic-test", ".yaml")
        tempFile.writeText(yamlContent)

        try {
            Engine(tempFile.absolutePath).use { engine ->
                val adminManager = engine.databaseAdministrationManager
                assertNotNull(adminManager.driver)
                assertTrue(adminManager.driver is TestDatabaseDriver)
            }
        } finally {
            tempFile.delete()
        }
    }
}
