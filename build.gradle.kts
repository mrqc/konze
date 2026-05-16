plugins {
    kotlin("jvm") version "2.3.10"
}

group = "net.masterstudios"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}

subprojects {
    plugins.withType<JavaPlugin> {
        apply(plugin = "maven-publish")

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])

                    groupId = "io.github.mrqc"
                    version = "0.1.0-SNAPSHOT"
                    artifactId = "konze-${project.name}"
                }
            }
        }
    }
}
