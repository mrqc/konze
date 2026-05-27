plugins {
    kotlin("jvm") version "2.3.10" apply false
    kotlin("plugin.spring") version "2.3.10" apply false
}

group = "net.masterstudios"
version = "0.1.0-SNAPSHOT"

subprojects {
    repositories {
        mavenCentral()
    }

    plugins.withType<JavaPlugin> {
        if (!project.name.contains("example")) {
            apply(plugin = "maven-publish")

            configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("mavenJava") {
                        from(components["java"])

                        groupId = rootProject.group.toString()
                        version = rootProject.version.toString()
                        artifactId = if (project.name.startsWith("konze")) project.name else "konze-${project.name}"
                    }
                }
            }
        }
    }
}
