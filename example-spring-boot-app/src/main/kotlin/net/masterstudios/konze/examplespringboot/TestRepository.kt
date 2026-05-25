package net.masterstudios.konze.examplespringboot
import org.springframework.data.jpa.repository.JpaRepository;
interface TestRepository : JpaRepository<TestEntity, Long> {
}
