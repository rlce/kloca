plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose) apply false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

allprojects {
    group = project.findProperty("project.group") as String
    
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        mavenLocal()
    }
}

subprojects {
    val kotlinVersion = project.findProperty("kotlin.version") as String? ?: "2.2.0"
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
            force("org.jetbrains.kotlin:kotlin-stdlib-common:$kotlinVersion")
            force("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
        }
    }
}

tasks.register("publishLocal") {
    group = "publishing"
    description = "Publish all modules to local Maven repository"
    dependsOn(":kloca-gradle-plugin:publishToMavenLocal", ":kloca-runtime:publishToMavenLocal", ":kloca-runtime-compose:publishToMavenLocal")
}
