package net.masterstudios.konze.examplespringboot

import jakarta.persistence.*
import org.springframework.data.domain.Persistable
import java.util.*

@Entity
class TestEntity(
    @Id
    @JvmField
    var id: UUID = UUID(0, 0),
    var name: String? = null
) : Persistable<UUID> {

    @Transient
    private var isNew: Boolean = true

    override fun isNew(): Boolean = this.isNew

    override fun getId(): UUID = id
    
    @PostLoad
    @PostPersist
    fun markNotNew() {
        this.isNew = false
    }
}
