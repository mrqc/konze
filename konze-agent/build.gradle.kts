plugins {
    kotlin("jvm") version "2.3.10"
}

// group and version removed to inherit from root

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("net.bytebuddy:byte-buddy:1.14.12")
    testImplementation(kotlin("test"))
}

tasks.jar {
    archiveClassifier.set("") 
    
    // Include all dependencies in the final JAR (Fat JAR)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes(
            "Premain-Class" to "net.masterstudios.konze.agent.DatabaseCommunicationAgent",
            "Can-Redefine-Classes" to "true",
            "Can-Retransform-Classes" to "true"
        )
    }
}

kotlin {
}

tasks.test {
    useJUnitPlatform()
}
