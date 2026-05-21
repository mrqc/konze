plugins {
    kotlin("jvm")
    `maven-publish`
}

group = "net.masterstudios"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}
