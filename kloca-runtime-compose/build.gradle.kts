@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.mavenPublish)
}

version = project.findProperty("kloca.runtime.compose.version") as String
group = project.findProperty("project.group") as String

kotlin {
    wasmJs {
        browser()
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
        publishLibraryVariants("release")
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "KlocaI18nRuntimeCompose"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":kloca-runtime"))
            implementation(compose.runtime)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.rlce.kloca.runtime.compose"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

mavenPublishing {
    configure(
        platform = KotlinMultiplatform(
            javadocJar = JavadocJar.None(),
            sourcesJar = true,
            androidVariantsToPublish = listOf("release"),
        )
    )
    coordinates(
        groupId = group.toString(),
        artifactId = "kloca-runtime-compose",
        version = version.toString()
    )

    pom {
        name.set(project.findProperty("kloca.runtime.compose.name") as String)
        description.set(project.findProperty("kloca.runtime.compose.description") as String)
        inceptionYear.set("2025")
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
