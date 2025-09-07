plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-gradle-plugin`
}

version = project.findProperty("kloca.gradle.plugin.version") as String

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
    mavenLocal()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.ksp.api)
    implementation(libs.android.gradle.plugin)
    implementation(libs.snakeyaml)
    implementation(libs.kotlinpoet)
    
    testImplementation(libs.bundles.kotlin.test)
    testImplementation(libs.junit.jupiter)
}

gradlePlugin {
    plugins {
        create("klocaPlugin") {
            id = "dev.rlce.kloca"
            group = project.findProperty("project.group") as String
            implementationClass = "dev.rlce.kloca.KlocaPlugin"
            displayName = project.findProperty("kloca.gradle.plugin.name") as String
            description = project.findProperty("kloca.gradle.plugin.description") as String
            tags.set(listOf("kotlin", "multiplatform", "localization", "i18n", "ksp"))
        }
    }
    website.set(project.findProperty("project.url") as String)
    vcsUrl.set(project.findProperty("project.vcs.url") as String)
}

kotlin {
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
}


apply(from = "${rootProject.projectDir}/gradle/scripts/maven-publish.gradle.kts")