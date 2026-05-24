package net.masterstudios.konze.examplespringboot

import net.masterstudios.konze.spring.DataSourceContextHolder.clearDataSourceType
import net.masterstudios.konze.spring.DataSourceContextHolder.setDataSourceType


class TestService {
    private var testRepository: TestRepository? = null

    fun TestService(testRepository: TestRepository) {
        this.testRepository = testRepository
    }

    fun process() {
        val a: Test = Test("Some Name")
        setDataSourceType("read-only-profile")
        try {
            testRepository?.save(a)
        } catch (e: Exception) {
            println("Exception while saving: ${e.message}")
        }
        setDataSourceType("full-access-profile")
        testRepository?.findAll()
        clearDataSourceType()
    }
}
