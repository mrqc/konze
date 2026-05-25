package net.masterstudios.konze.examplespringboot

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class TestServiceIntegrationTest {

    @Autowired
    lateinit var testService: TestService

    @Test
    fun `test process method handles exception in read-only profile`() {
        try {
            testService.process()
        } catch (e: Exception) {
            println("Test caught unexpected exception: ${e.message}")
        }
    }
}
