plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    api(project(":konze-core"))
    implementation("org.springframework.boot:spring-boot-autoconfigure:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.2.5")
    implementation("org.springframework.boot:spring-boot-starter-web:3.2.5")
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
}

kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}
