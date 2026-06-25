plugins {
    kotlin("jvm") version "2.3.10"
    application
}

val agentJarTask = project(":konze-agent").tasks.named<Jar>("jar")

application {
    mainClass.set("net.masterstudios.net.masterstudios.konze.cli.CLI")
}

tasks.named<CreateStartScripts>("startScripts") {
    doLast {
        val agentJarName = agentJarTask.get().archiveFileName.get()

        // Unix: inject -javaagent into the `set --` block where $APP_HOME is expanded
        unixScript.writeText(
            unixScript.readText().replace(
                "-classpath \"\$CLASSPATH\"",
                "\"-javaagent:\$APP_HOME/lib/$agentJarName\" \\\n        -classpath \"\$CLASSPATH\""
            )
        )

        // Windows: inject -javaagent into the java command line
        windowsScript.writeText(
            windowsScript.readText().replace(
                "-classpath \"%CLASSPATH%\"",
                "\"-javaagent:%APP_HOME%\\lib\\$agentJarName\" -classpath \"%CLASSPATH%\""
            )
        )
    }
}

distributions {
    main {
        contents {
            from(agentJarTask) {
                into("lib")
            }
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
}

group = "net.master-studios"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":konze-core"))
    implementation(project(":konze-driver-postgres"))
    implementation("com.github.ajalt.clikt:clikt:5.1.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(22)
}

tasks.test {
    useJUnitPlatform()
}
