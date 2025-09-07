plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
}

version = project.findProperty("kloca.runtime.compose.version") as String

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
        publishLibraryVariants("release", "debug")
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
    compileSdk = 35

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

apply(from = "${rootProject.projectDir}/gradle/scripts/maven-publish.gradle.kts")