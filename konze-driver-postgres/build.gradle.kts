plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":konze-core"))
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}
