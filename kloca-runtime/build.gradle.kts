import com.vanniktech.maven.publish.KotlinMultiplatform
import com.vanniktech.maven.publish.JavadocJar

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.vanniktech.mavenPublish)
}

version = project.findProperty("kloca.runtime.version") as String
group = project.findProperty("project.group") as String

kotlin {
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
        artifactId = "kloca-runtime",
        version = version.toString()
    )

    pom {
        name.set(project.findProperty("kloca.runtime.name") as String)
        description.set(project.findProperty("kloca.runtime.description") as String)
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
