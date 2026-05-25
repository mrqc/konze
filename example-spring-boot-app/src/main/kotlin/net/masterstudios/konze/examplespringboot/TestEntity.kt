package net.masterstudios.konze.examplespringboot

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
class TestEntity (
    @Id
    var id: UUID? = null,
    var name: String? = null
)
