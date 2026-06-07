plugins {
    kotlin("jvm") version "2.3.10" apply false
    kotlin("plugin.spring") version "2.3.10" apply false
    id("maven-publish")
    id("signing")
}

group = "net.master-studios"
version = "0.1.0"

subprojects {
    group = rootProject.group
    version = rootProject.version
    repositories {
        mavenCentral()
    }

    plugins.withType<JavaPlugin> {
        if (!project.name.contains("example")) {
            apply(plugin = "maven-publish")
            apply(plugin = "signing")

            configure<JavaPluginExtension> {
                withSourcesJar()
                withJavadocJar()
            }

            configure<PublishingExtension> {
                publications {
                    create<MavenPublication>("mavenJava") {
                        from(components["java"])

                        groupId = project.group.toString()
                        version = project.version.toString()
                        artifactId = if (project.name.startsWith("konze")) project.name else "konze-${project.name}"

                        pom {
                            name.set(project.name)
                            description.set("Konze: A robust database connection management framework for Java and Kotlin.")
                            url.set("https://github.com/mrqc/konze")
                            licenses {
                                license {
                                    name.set("MIT License")
                                    url.set("https://opensource.org/licenses/MIT")
                                }
                            }
                            developers {
                                developer {
                                    id.set("mrqc")
                                    name.set("mrqc")
                                    email.set("office@masterstudios.net")
                                }
                            }
                            scm {
                                connection.set("scm:git:git://github.com/mrqc/konze.git")
                                developerConnection.set("scm:git:ssh://github.com/mrqc/konze.git")
                                url.set("https://github.com/mrqc/konze")
                            }
                        }
                    }
                }

                repositories {
                    maven {
                        val isSnapshot = project.version.toString().endsWith("SNAPSHOT")
                        val releasesRepoUrl = uri("https://central.sonatype.com/repository/maven-releases/")
                        val snapshotsRepoUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
                        url = if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl
                        credentials {
                            username = project.findProperty("sonatypeUsername")?.toString()
                            password = project.findProperty("sonatypePassword")?.toString()
                        }
                    }
                }
            }

            configure<SigningExtension> {
                sign(extensions.getByType<PublishingExtension>().publications["mavenJava"])
            }
        }
    }
}
