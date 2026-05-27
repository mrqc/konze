plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
rootProject.name = "konze"

include(":konze-core")
include(":konze-driver-postgres")
include(":konze-spring-boot-starter")
include(":example-app")

include("example-spring-boot-app")
include("konze-agent")