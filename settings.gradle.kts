plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "konze"
include(":core")
include(":driver-postgres")

include("core")
include("driver-postgres")