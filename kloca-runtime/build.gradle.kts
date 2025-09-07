plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
}

version = project.findProperty("kloca.runtime.version") as String

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
            baseName = "KlocaI18nRuntime"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(kotlin("stdlib"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        }

        androidUnitTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.robolectric)
        }

        iosTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "dev.rlce.kloca.runtime"
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
