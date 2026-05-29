plugins {
    kotlin("jvm")
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":konze-core"))
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("us.fatehi:schemacrawler-postgresql:17.11.1")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
    explicitApi()
}

tasks.test {
    useJUnitPlatform()
}
