package net.masterstudios.konze.yaml

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YamlFileReaderTest {

    data class TestConfig(val key: String, val list: List<String>)

    @Test
    fun `should read yaml file into map`() {
        val yamlContent = """
            key: value
            list:
              - item1
              - item2
        """.trimIndent()
        
        val tempFile = File.createTempFile("test", ".yaml")
        tempFile.writeText(yamlContent)
        
        try {
            val reader = YamlFileReader(tempFile.absolutePath)
            val result = reader.read()
            
            assertEquals("value", result["key"])
            val list = result["list"] as List<*>
            assertEquals(2, list.size)
            assertEquals("item1", list[0])
            assertEquals("item2", list[1])
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `should read yaml file into data class`() {
        val yamlContent = """
            key: value
            list:
              - item1
              - item2
        """.trimIndent()
        
        val tempFile = File.createTempFile("test", ".yaml")
        tempFile.writeText(yamlContent)
        
        try {
            val reader = YamlFileReader(tempFile.absolutePath)
            val result = reader.readAs<TestConfig>()
            
            assertEquals("value", result.key)
            assertEquals(2, result.list.size)
            assertEquals("item1", result.list[0])
            assertEquals("item2", result.list[1])
        } finally {
            tempFile.delete()
        }
    }

    @Test
    fun `should read example-spec yaml file`() {
        val reader = YamlFileReader("../example-app/src/main/resources/example-spec.yaml")
        val result = reader.readAs<ConfigurationFile>()
        
        val profiles = result.konze.profiles
        assertEquals(3, profiles.size)
        assertTrue(profiles.containsKey("full-access-profile"))
        assertTrue(profiles.containsKey("read-only-profile"))
        assertTrue(profiles.containsKey("write-only-profile"))
        
        val profile = profiles["full-access-profile"]!!
        assertEquals(13, profile.permissions.size)
        assertTrue(profile.permissions.contains(Permission.SELECT))
        assertTrue(profile.permissions.contains(Permission.INSERT))
        
        assertEquals("60s", profile.configuration.query.executionTimeout)
        assertTrue(profile.configuration.query.executionLogging)
        
        assertEquals("./logs/audit.log", profile.configuration.audit.log)
        
        assertEquals("500ms", profile.configuration.monitoring.slowQueryThreshold)
        
        assertTrue(profile.schemaDiscoveryEndpoint.enabled)
        assertEquals("/schema-discovery", profile.schemaDiscoveryEndpoint.endpoint)
        
        assertEquals("jdbc:postgresql://localhost:5432/konze_db", profile.pool.jdbcUrl)
        assertEquals(10, profile.pool.maximumPoolSize)

        assertNotNull(result.konze.databaseAdministration)
        val admin = result.konze.databaseAdministration!!
        assertTrue(admin.schema.updateTrigger)
        assertTrue(admin.schema.deleteTrigger)
        assertTrue(admin.schema.insertTrigger)
    }

    @Test
    fun `should read minimal-spec yaml file`() {
        val reader = YamlFileReader("../example-app/src/main/resources/minimal-spec.yaml")
        val result = reader.readAs<ConfigurationFile>()
        
        val profiles = result.konze.profiles
        assertEquals(1, profiles.size)
        assertTrue(profiles.containsKey("example-profile"))
        
        val profile = profiles["example-profile"]!!
        assertEquals(1, profile.permissions.size)
        assertEquals(Permission.SELECT, profile.permissions[0])
        
        assertNotNull(result.konze.databaseAdministration)
        assertEquals("net.masterstudios.konze.driver.postgres.PostgresDatabaseDriver", result.konze.databaseAdministration?.access?.driver)
    }

    private fun assertNotNull(actual: Any?) {
        assertTrue(actual != null)
    }
}
