package net.masterstudios.konze.examplespringboot

import net.masterstudios.konze.spring.DataSourceContextHolder.clearDataSourceType
import net.masterstudios.konze.spring.DataSourceContextHolder.setDataSourceType
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TestService(private val testRepository: TestRepository) {

    fun process() {
        val a = TestEntity(id = UUID.randomUUID(), name = "Some Name")
        setDataSourceType("read-only-profile")
        try {
            testRepository.save(a)
        } catch (e: Exception) {
            println("Exception while saving: ${e.message}")
        }
        testRepository.findAll()
        setDataSourceType("write-only-profile")
        testRepository.save(a)
        try {
            testRepository.findAll()
        } catch (e: Exception) {
            println("Exception while reading: ${e.message}")
        }
        
        setDataSourceType("full-access-profile")
        testRepository.findAll()
        testRepository.deleteAll()
        clearDataSourceType()
    }
}
