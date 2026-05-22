package net.masterstudios.konze

import net.masterstudios.konze.database.TestDatabaseDriver
import java.io.File
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.test.assertFailsWith

class EngineTest {

    @Test
    fun `should initialize engine with multiple contexts`() {
        val yamlContent = """
            konze:
              databaseContextId: test-db
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
            Engine(listOf(tempFile.absolutePath)).use { engine ->
                val context = engine.getDatabaseContext("test-db")
                assertNotNull(context)
                val pool = context.poolManager.getPool("test-profile")
                assertNotNull(pool)
            }
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `should fail if databaseContextId is missing`() {
        val yamlContent = """
            konze:
              profiles:
                test-profile:
                  pool:
                    jdbcUrl: jdbc:h2:mem:fail_test;DB_CLOSE_DELAY=-1
                    username: sa
                    password: ""
                    initializationFailTimeout: -1
              databaseAdministration:
                access:
                  driver: net.masterstudios.konze.database.TestDatabaseDriver
                  jdbcUrl: jdbc:h2:mem:fail_test;DB_CLOSE_DELAY=-1
                  username: sa
                  password: ""
        """.trimIndent()

        val tempFile = File.createTempFile("fail-test", ".yaml")
        tempFile.writeText(yamlContent)

        try {
            assertFailsWith<IllegalArgumentException> {
                Engine(listOf(tempFile.absolutePath))
            }
        } finally {
            tempFile.delete()
        }
    }
}
