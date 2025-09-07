pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}

rootProject.name = "kloca"

include(":kloca-gradle-plugin")
include(":kloca-runtime")
include(":kloca-runtime-compose")
include(":sample")