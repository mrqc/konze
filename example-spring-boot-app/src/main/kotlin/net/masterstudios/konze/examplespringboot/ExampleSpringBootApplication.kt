package net.masterstudios.konze.examplespringboot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ExampleSpringBootApplication

fun main(args: Array<String>) {
    runApplication<ExampleSpringBootApplication>(*args)
}
