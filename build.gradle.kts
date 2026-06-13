plugins {
    kotlin("jvm") version "2.3.10" apply false
    kotlin("plugin.spring") version "2.3.10" apply false
    id("maven-publish")
    id("signing")
}

group = "net.master-studios"
version = "0.1.0"

subprojects {
    project.group = rootProject.group
    project.version = rootProject.version

    repositories {
        mavenCentral()
    }

    if (project.name.startsWith("konze")) {
        apply(plugin = "maven-publish")
        apply(plugin = "signing")
        apply(plugin = "java-library")

        configure<JavaPluginExtension> {
            withSourcesJar()
            withJavadocJar()
        }

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("mavenJava") {
                    from(components["java"])
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()
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
                                email.set("info@masterstudios.net")
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
                // A local repository that we will use to generate the ZIP bundle
                maven {
                    name = "Bundle"
                    url = uri(rootProject.layout.buildDirectory.dir("bundle"))
                }
            }
        }

        configure<SigningExtension> {
            sign(extensions.getByType<PublishingExtension>().publications["mavenJava"])
        }
    }
}

// Task to create a deployment bundle for Maven Central Portal
tasks.register<Zip>("zipBundle") {
    description = "Creates a ZIP bundle for manual upload to Maven Central Portal"
    group = "publishing"
    
    subprojects.forEach { sub ->
        if (sub.name.startsWith("konze")) {
            // This ensures we wait for the subproject to publish to the local bundle folder
            dependsOn(sub.tasks.matching { it.name == "publishMavenJavaPublicationToBundleRepository" })
        }
    }
    
    archiveFileName.set("konze-bundle-${project.version}.zip")
    destinationDirectory.set(layout.buildDirectory.dir("distributions"))
    
    from(layout.buildDirectory.dir("bundle"))
}
