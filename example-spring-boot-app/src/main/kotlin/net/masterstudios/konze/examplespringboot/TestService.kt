package net.masterstudios.konze.examplespringboot

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class TestService(
    private val testRepository: TestRepository,
    private val jdbcTemplate: JdbcTemplate
) {

    @Transactional
    fun createTestEntity2() {
        jdbcTemplate.execute("create table if not exists public.test_entity2 (id uuid primary key, name varchar(255))")
    }

    @Transactional
    fun dropTestEntity2() {
        jdbcTemplate.execute("drop table if exists public.test_entity2")
    }

    @Transactional
    fun findAllAndDeleteAll() {
        // Do a read
        testRepository.findAll()
        // Do a delete
        testRepository.deleteAll()
    }

    @Transactional
    fun doASave() {
        // Generate a test entity instance
        val testEntity = TestEntity(id = UUID.randomUUID(), name = "Some Name")
        // Store the test entity instance, which works now
        testRepository.save(testEntity)
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
        val testEntity = TestEntity(id = UUID.randomUUID(), name = "Some Name")
        // Store the test entity instance, which works now
        testRepository.save(testEntity)
    }
}
