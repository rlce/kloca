plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose)
    alias(libs.plugins.kotlin.compose)
    id("dev.rlce.kloca") version "0.1.0"
}

kotlin {
    androidTarget {
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
                implementation("dev.rlce.kloca:kloca-runtime:${project.findProperty("kloca.runtime.version")}")
                implementation("dev.rlce.kloca:kloca-runtime-compose:${project.findProperty("kloca.runtime.compose.version")}")
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.runtime)
                implementation("org.jetbrains.androidx.navigation:navigation-compose:2.8.0-alpha13")
            }
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
        }
    }
}

android {
    namespace = "dev.rlce.kloca.sample"
    compileSdk = 35

    defaultConfig {
        applicationId = "dev.rlce.kloca.sample"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    buildFeatures {
        compose = true
    }

    kloca {
        defaultLanguage = "en"
        namespacePrefix = "sample"
    }
}
