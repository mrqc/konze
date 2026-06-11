package net.masterstudios.konze.examplespringboot

import net.masterstudios.konze.spring.DataSourceContextHolder.clearDataSourceType
import net.masterstudios.konze.spring.DataSourceContextHolder.setDataSourceType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.assertFalse

@SpringBootTest
class TestServiceIntegrationTest {

    @Autowired
    lateinit var testService: TestService
    
    @Test
    fun `test process method handles exception in profile`() {
        try {
            setDataSourceType("read-only-profile")
            testService.createTestEntity2()
        } catch (ex: Exception) {
            if ("permission" in (ex.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with read-only profile: ${ex.message}")
            } else {
                throw ex
            }        }
        try {
            setDataSourceType("write-only-profile")
            testService.createTestEntity2()
        } catch (ex: Exception) {
            if ("permission" in (ex.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with write-only profile: ${ex.message}")
            } else {
                throw ex
            }        
        }
        setDataSourceType("full-access-profile")
        testService.createTestEntity2()

        // Set the database access to read only profile
        setDataSourceType("read-only-profile")
        testService.doARead()
        try {
            testService.doASave()
            assertFalse(true, "doASave must throw an exception when using a read-only profile")
        } catch (e: Exception) {
            if ("permission" in (e.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with read-only profile: ${e.message}")
            } else {
                throw e
            }        
        }
        try {
            testService.doReadAndWrite()
            assertFalse(true, "doReadAndWrite must throw an exception when using a read-only profile")
        } catch (e: Exception) {
            if ("permission" in (e.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with write-only profile: ${e.message}")
            } else {
                throw e
            }   
        }

        // Switch the profile to write only
        setDataSourceType("write-only-profile")
        testService.doASave()
        try {
            testService.doARead()
            assertFalse(true, "doARead must throw an exception when using a write only-profile")
        } catch (e: Exception) {
            if ("permission" in (e.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with write-only profile: ${e.message}")
            } else {
                throw e
            }   
        }
        try {
            testService.doReadAndWrite()
            assertFalse(true, "doReadAndWrite must throw an exception when using a read-only profile")
        } catch (e: Exception) {
            if ("permission" in (e.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with write-only profile: ${e.message}")
            } else {
                throw e
            }   
        }

        // Switch to a full access profile
        setDataSourceType("full-access-profile")
        testService.findAllAndDeleteAll()
        
        // CLEANUP
        try {
            setDataSourceType("read-only-profile")
            testService.dropTestEntity2()
        } catch (ex: Exception) {
            if ("permission" in (ex.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with read-only profile: ${ex.message}")
            } else {
                throw ex
            }
        }
        try {
            setDataSourceType("write-only-profile")
            testService.dropTestEntity2()
        } catch (ex: Exception) {
            if ("permission" in (ex.cause?.message?.lowercase() ?: "")) {
                println("Expected exception when trying to create test entity with write-only profile: ${ex.message}")
            } else {
                throw ex
            }        
        }
        setDataSourceType("full-access-profile")
        testService.dropTestEntity2()

        clearDataSourceType()
    }
    
    @Test
    fun test() {
        try {
            this.`test process method handles exception in profile`()
        } catch (e: Exception) {
            println("Test caught unexpected exception: ${e.message}")
            assertFalse(true, "process method must handle exceptions internally and not throw them")
        }
    }
}
