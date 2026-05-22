package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HikariPoolManagerTest {

    private fun createConfig(profiles: Map<String, ProfileConfiguration>): ConfigurationFile {
        return ConfigurationFile(
            konze = Konze(
                profiles = profiles,
                databaseAdministration = DatabaseAdministration(
                    access = DatabaseAccess(
                        driver = "net.masterstudios.konze.database.TestDatabaseDriver",
                        jdbcUrl = "jdbc:h2:mem:admin_test;DB_CLOSE_DELAY=-1",
                        username = "sa",
                        password = ""
                    )
                )
            )
        )
    }

    @Test
    fun `should create pools for each profile`() {
        val config = createConfig(
            mapOf(
                "profile1" to ProfileConfiguration(
                    pool = PoolConfiguration(
                        jdbcUrl = "jdbc:h2:mem:test1;DB_CLOSE_DELAY=-1",
                        username = "sa",
                        password = "",
                        maximumPoolSize = 5,
                        initializationFailTimeout = -1
                    )
                ),
                "profile2" to ProfileConfiguration(
                    pool = PoolConfiguration(
                        jdbcUrl = "jdbc:h2:mem:test2;DB_CLOSE_DELAY=-1",
                        username = "sa",
                        password = "",
                        maximumPoolSize = 3,
                        initializationFailTimeout = -1
                    )
                )
            )
        )

        val adminManager = DatabaseAdministrationManager(config)
        HikariPoolManager(config, adminManager).use { manager ->
            val pools = manager.getAllPools()
            assertEquals(2, pools.size)
            assertNotNull(manager.getPool("profile1"))
            assertNotNull(manager.getPool("profile2"))
            
            assertEquals(5, manager.getPool("profile1")?.hikariConfigMXBean?.maximumPoolSize)
            assertEquals(3, manager.getPool("profile2")?.hikariConfigMXBean?.maximumPoolSize)
            assertTrue(manager.getPool("profile1")?.poolName?.contains("profile1") == true)
        }
    }

    @Test
    fun `should generate password when missing from configuration`() {
        val config = createConfig(
            mapOf(
                "profile1" to ProfileConfiguration(
                    pool = PoolConfiguration(
                        jdbcUrl = "jdbc:h2:mem:gen_pwd_test;DB_CLOSE_DELAY=-1",
                        username = "sa",
                        password = null, // Trigger generation
                        initializationFailTimeout = -1
                    )
                )
            )
        )

        val adminManager = DatabaseAdministrationManager(config)
        HikariPoolManager(config, adminManager).use { manager ->
            val pool = manager.getPool("profile1")
            assertNotNull(pool)
        }
    }
}
