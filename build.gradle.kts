plugins {
    kotlin("jvm") version "2.3.10" apply false
}

group = "net.masterstudios"
version = "1.0-SNAPSHOT"

subprojects {
    repositories {
        mavenCentral()
    }

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
