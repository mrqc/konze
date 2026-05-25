package net.masterstudios.konze.examplespringboot

import net.masterstudios.konze.spring.DataSourceContextHolder.clearDataSourceType
import net.masterstudios.konze.spring.DataSourceContextHolder.setDataSourceType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertFalse

@SpringBootTest
class TestServiceIntegrationTest {

    @Autowired
    lateinit var testService: TestService
    
    @Test
    fun `test process method handles exception in profile`() {
        // Set the database access to read only profile
        setDataSourceType("read-only-profile")
        testService.doARead()
        try {
            testService.doASave()
            assertFalse(true, "doASave should throw an exception when using a read-only profile")
        } catch (e: Exception) {
            println("Caught expected exception when trying to save with read-only profile: ${e.message}")
        }
        try {
            testService.doReadAndWrite()
            assertFalse(true, "doReadAndWrite should throw an exception when using a read-only profile")
        } catch (e: Exception) {
            println("Caught expected exception when trying to read and write with read-only profile: ${e.message}")
        }

        // Switch the profile to write only
        setDataSourceType("write-only-profile")
        testService.doASave()
        try {
            testService.doARead()
            assertFalse(true, "doARead should throw an exception when using a write only-profile")
        } catch (e: Exception) {
            println("Caught expected exception when trying to save with write only-profile")
        }
        try {
            testService.doReadAndWrite()
            assertFalse(true, "doReadAndWrite should throw an exception when using a read-only profile")
        } catch (e: Exception) {
            println("Caught expected exception when trying to read and write with read-only profile: ${e.message}")
        }

        // Switch to a full access profile
        setDataSourceType("full-access-profile")
        testService.fullAccessProfile()

        clearDataSourceType()
    }
    
    @Test
    fun test() {
        try {
            this.`test process method handles exception in profile`()
        } catch (e: Exception) {
            println("Test caught unexpected exception: ${e.message}")
            assertFalse(true, "process method should handle exceptions internally and not throw them")
        }
    }
}
