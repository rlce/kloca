import java.util.Properties

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

rootProject.name = "kloca-gradle-plugin"

val sharedProperties = Properties().apply {
    file("../gradle.properties").inputStream().use(::load)
}
gradle.beforeProject {
    sharedProperties.forEach { (key, value) ->
        extensions.extraProperties[key.toString()] = value.toString()
    }
}
