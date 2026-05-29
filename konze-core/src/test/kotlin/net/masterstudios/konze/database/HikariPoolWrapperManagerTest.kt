package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HikariPoolWrapperManagerTest {

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
            
            assertEquals(5, manager.getPool("profile1")?.hikariDataSource?.hikariConfigMXBean?.maximumPoolSize)
            assertEquals(3, manager.getPool("profile2")?.hikariDataSource?.hikariConfigMXBean?.maximumPoolSize)
            assertTrue(manager.getPool("profile1")?.hikariDataSource?.poolName?.contains("profile1") == true)
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

    @Test
    fun `should get pool from connection`() {
        val config = createConfig(
            mapOf(
                "profile1" to ProfileConfiguration(
                    pool = PoolConfiguration(
                        jdbcUrl = "jdbc:h2:mem:get_pool_test;DB_CLOSE_DELAY=-1",
                        username = "sa",
                        password = "",
                        initializationFailTimeout = -1,
                        schema = null
                    )
                )
            )
        )

        val adminManager = DatabaseAdministrationManager(config)
        HikariPoolManager(config, adminManager).use { manager ->
            val pool = manager.getPool("profile1")
            assertNotNull(pool)
            
            val connection = pool.hikariDataSource.connection
            try {
                val detectedPool = manager.getPoolFromConnection(connection)
                assertEquals(pool, detectedPool)
            } finally {
                connection.close()
            }
        }
    }
}
