
plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":konze-core"))
    implementation(project(":konze-driver-postgres"))
    implementation("com.h2database:h2:2.2.224")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("org.slf4j:slf4j-simple:2.0.13")
}

application {
    mainClass.set("net.masterstudios.konze.exampleapp.ExampleApplication")
}

kotlin {
    jvmToolchain(22)
}
