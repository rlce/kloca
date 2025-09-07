package dev.rlce.kloca.sample

import android.app.Application
import dev.rlce.kloca.runtime.DefaultAndroidLanguagePersistence
import dev.rlce.kloca.runtime.startKloca

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Kloca with user language selection support
        // - Context: Pass application context for resource access
        // - Available languages: English and Spanish translations
        // - Language persistence: Uses Android SharedPreferences to save user's preferred language
        startKloca(
            context = this,
            availableLanguages = arrayOf("en", "es"),
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(this) },
        )
    }
}
