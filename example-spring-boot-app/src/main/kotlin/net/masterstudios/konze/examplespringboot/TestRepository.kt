package net.masterstudios.konze.examplespringboot
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID

interface TestRepository : JpaRepository<TestEntity, UUID> {
}
