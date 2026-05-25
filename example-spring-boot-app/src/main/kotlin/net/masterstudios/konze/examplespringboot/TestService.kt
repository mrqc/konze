package net.masterstudios.konze.examplespringboot

import net.masterstudios.konze.spring.DataSourceContextHolder.clearDataSourceType
import net.masterstudios.konze.spring.DataSourceContextHolder.setDataSourceType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TestService(private val testRepository: TestRepository) {
    
    @Transactional
    fun fullAccessProfile() {
        // Do a read
        testRepository.findAll()
        // Do a delete
        testRepository.deleteAll()
    }

    @Transactional
    fun doASave() {
        // Generate a test entity instance
        val a = TestEntity(id = UUID.randomUUID(), name = "Some Name")
        // Store the test entity instance, which works now
        testRepository.save(a)
    }

    @Transactional
    fun doARead() {
        // Test the read only profile to access the database
        testRepository.findAll()
    }

    @Transactional
    fun doReadAndWrite() {
        // Test the read only profile to access the database
        testRepository.findAll()
        // Generate a test entity instance
        val a = TestEntity(id = UUID.randomUUID(), name = "Some Name")
        // Store the test entity instance, which works now
        testRepository.save(a)
    }
}
