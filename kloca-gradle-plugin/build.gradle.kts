import com.vanniktech.maven.publish.GradlePublishPlugin

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("com.gradle.plugin-publish") version "2.0.0"
    alias(libs.plugins.vanniktech.mavenPublish)
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
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

version = project.findProperty("kloca.gradle.plugin.version") as String
group = project.findProperty("project.group") as String

gradlePlugin {
    website.set(project.findProperty("project.url") as String)
    vcsUrl.set(project.findProperty("project.vcs.url") as String)
    plugins {
        create("klocaPlugin") {
            id = "io.github.rlce.kloca"
            implementationClass = "dev.rlce.kloca.KlocaPlugin"
            displayName = project.findProperty("kloca.gradle.plugin.name") as String
            description = project.findProperty("kloca.gradle.plugin.description") as String
            tags = listOf("kotlin", "multiplatform", "localization", "i18n", "ksp")
        }
    }
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

mavenPublishing {
    configure(GradlePublishPlugin())

    coordinates(
        groupId = group.toString(),
        artifactId = "kloca-gradle-plugin",
        version = version.toString()
    )

    pom {
        name.set(project.findProperty("kloca.gradle.plugin.name") as String)
        description.set(project.findProperty("kloca.gradle.plugin.description") as String)
        inceptionYear.set("2024")
        url.set(project.findProperty("project.url") as String)

        licenses {
            license {
                name.set(project.findProperty("project.license.name") as String)
                url.set(project.findProperty("project.license.url") as String)
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set(project.findProperty("project.developer.id") as String)
                name.set(project.findProperty("project.developer.name") as String)
                email.set(project.findProperty("project.developer.email") as String)
            }
        }

        scm {
            url.set(project.findProperty("project.url") as String)
            connection.set(project.findProperty("project.vcs.connection") as String)
            developerConnection.set(project.findProperty("project.vcs.developerConnection") as String)
        }
    }

    publishToMavenCentral()
    signAllPublications()
}
