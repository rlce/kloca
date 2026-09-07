pluginManagement {
    includeBuild("kloca-gradle-plugin")
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

include(":kloca-runtime")
include(":kloca-runtime-compose")
include(":sample")
include(":sample-android-app")
