plugins {
    kotlin("jvm")
    `maven-publish`
    `java-library`
}

group = "net.masterstudios"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.1")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.1")
    api("com.zaxxer:HikariCP:5.1.0")
    testImplementation(kotlin("test"))
    testImplementation("com.h2database:h2:2.2.224")
    testImplementation(project(":driver-h2"))
}

kotlin {
    jvmToolchain(22)
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}
