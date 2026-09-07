plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.rlce.kloca.sample.app"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "dev.rlce.kloca.sample"
        targetSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":sample"))
}
