plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose) apply false
}

allprojects {
    group = project.findProperty("project.group") as String
    
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        mavenCentral()
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

tasks.register("publishPlugin") {
    group = "publishing"
    description = "Publish gradle plugin to repositories (plugin lifecycle)"
    dependsOn(":kloca-gradle-plugin:publishToMavenLocal", ":kloca-gradle-plugin:publish")
}

tasks.register("publishRuntime") {
    group = "publishing"
    description = "Publish runtime library to repositories (runtime lifecycle)"
    dependsOn(":kloca-runtime:publishToMavenLocal", ":kloca-runtime:publish")
}

tasks.register("publishRuntimeCompose") {
    group = "publishing"
    description = "Publish runtime compose library to repositories (compose runtime lifecycle)"
    dependsOn(":kloca-runtime-compose:publishToMavenLocal", ":kloca-runtime-compose:publish")
}

tasks.register("publishLocal") {
    group = "publishing"
    description = "Publish all modules to local Maven repository"
    dependsOn(":kloca-gradle-plugin:publishToMavenLocal", ":kloca-runtime:publishToMavenLocal", ":kloca-runtime-compose:publishToMavenLocal")
}

tasks.register("publishRemote") {
    group = "publishing"
    description = "Publish all modules to remote Maven repository"
    dependsOn(":kloca-gradle-plugin:publish", ":kloca-runtime:publish", ":kloca-runtime-compose:publish")
}
