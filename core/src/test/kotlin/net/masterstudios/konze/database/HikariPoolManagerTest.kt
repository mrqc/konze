package net.masterstudios.konze.database

import net.masterstudios.konze.yaml.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class HikariPoolManagerTest {

    @Test
    fun `should create pools for each profile`() {
        val config = ConfigurationFile(
            konze = Konze(
                profiles = mapOf(
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
        )

        HikariPoolManager(config).use { manager ->
            val pools = manager.getAllPools()
            assertEquals(2, pools.size)
            assertNotNull(manager.getPool("profile1"))
            assertNotNull(manager.getPool("profile2"))
            
            assertEquals(5, manager.getPool("profile1")?.hikariConfigMXBean?.maximumPoolSize)
            assertEquals(3, manager.getPool("profile2")?.hikariConfigMXBean?.maximumPoolSize)
            assertTrue(manager.getPool("profile1")?.poolName?.contains("profile1") == true)
        }
    }
}
