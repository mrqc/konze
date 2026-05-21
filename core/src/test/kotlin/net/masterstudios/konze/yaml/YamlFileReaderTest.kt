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
        assertEquals(1, profiles.size)
        assertTrue(profiles.containsKey("example-profile"))
        
        val profile = profiles["example-profile"]!!
        assertEquals(14, profile.permissions.size)
        
        assertEquals(1000, profile.configuration.query.rowLimit)
        assertEquals("60s", profile.configuration.query.executionTimeout)
        assertTrue(profile.configuration.query.executionLogging)
        
        assertEquals("./logs/audit.log", profile.configuration.audit.log)
        
        assertEquals("500ms", profile.configuration.monitoring.slowQueryThreshold)
        
        assertTrue(profile.schemaDiscoveryEndpoint.enabled)
        assertEquals("/schema-discovery", profile.schemaDiscoveryEndpoint.endpoint)
        
        assertEquals("jdbc:h2:mem:konze_example;DB_CLOSE_DELAY=-1", profile.pool.jdbcUrl)
        assertEquals(10, profile.pool.maximumPoolSize)

        assertNotNull(result.konze.databaseAdministration)
        assertEquals(3, result.konze.databaseAdministration!!.schema.size)
    }

    private fun assertNotNull(actual: Any?) {
        assertTrue(actual != null)
    }
}
