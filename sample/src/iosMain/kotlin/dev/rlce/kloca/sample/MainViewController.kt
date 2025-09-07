package dev.rlce.kloca.sample

import androidx.compose.ui.window.ComposeUIViewController
import dev.rlce.kloca.runtime.DefaultIosLanguagePersistence
import dev.rlce.kloca.runtime.startKloca
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    // Initialize Kloca with user language selection support for iOS
    // - Context: Unit (no context needed for iOS)
    // - Available languages: English and Spanish translations
    // - Language persistence: Uses iOS NSUserDefaults to save user's preferred language
    startKloca(
        context = Unit,
        availableLanguages = arrayOf("en", "es"),
        languagePersistenceFactory = { DefaultIosLanguagePersistence() },
    )

    return ComposeUIViewController {
        SampleApp()
    }
}
