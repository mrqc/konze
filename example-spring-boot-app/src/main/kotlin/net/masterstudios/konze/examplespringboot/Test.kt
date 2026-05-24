package net.masterstudios.konze.examplespringboot

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id;

class Test (name: String) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    private var name: String? = null

    init {
        this.name = name
    }
}
