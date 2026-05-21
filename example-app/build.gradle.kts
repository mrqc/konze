
plugins {
    kotlin("jvm")
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":core"))
    implementation(project(":driver-h2"))
    implementation("com.h2database:h2:2.2.224")
    implementation("org.slf4j:slf4j-simple:2.0.13")
}

application {
    mainClass.set("net.masterstudios.konze.exampleapp.ExampleApplication")
}

kotlin {
    jvmToolchain(22)
}
