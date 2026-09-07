@file:OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    id("io.github.rlce.kloca")
}

kotlin {
    wasmJs {
        browser()
        binaries.executable()
    }

    android {
        namespace = "dev.rlce.kloca.sample"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        androidResources {
            enable = true
        }
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":kloca-runtime"))
                implementation(project(":kloca-runtime-compose"))
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.runtime)
            }
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

kloca {
    defaultLanguage = "en"
    namespacePrefix = "sample"
}
