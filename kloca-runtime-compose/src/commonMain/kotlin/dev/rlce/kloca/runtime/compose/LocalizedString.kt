package dev.rlce.kloca.runtime.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import dev.rlce.kloca.runtime.Kloca

/**
 * Composable function to get a localized string.
 * Automatically uses the correct provider based on Kloca initialization:
 * - Uses SystemLanguageProvider when no userLanguages specified
 * - Uses UserLanguageProvider when userLanguages were provided
 * - Automatically recomposes when language changes
 * - Uses Flow-based reactive updates for optimal performance
 * This is the recommended approach for all applications.
 *
 * @param key The translation key
 * @return The localized string or the key as fallback
 */
@Composable
fun localizedString(key: String): String {
    val languageState by Kloca.languageStateFlow().collectAsState(
        initial = Kloca.LanguageState(
            Kloca.getLanguageMode(),
            Kloca.getCurrentLanguage(),
        ),
    )

    return remember(key, languageState) {
        Kloca.getString(key)
    }
}

/**
 * Composable function to get a localized string with format arguments.
 * Automatically uses the correct provider based on Kloca initialization:
 * - Uses SystemLanguageProvider when no userLanguages specified
 * - Uses UserLanguageProvider when userLanguages were provided
 * - Automatically recomposes when language changes
 * - Uses Flow-based reactive updates for optimal performance
 * This is the recommended approach for all applications.
 *
 * @param key The translation key
 * @param args Format arguments for the string
 * @return The localized formatted string or the key as fallback
 */
@Composable
fun localizedString(key: String, vararg args: Any): String {
    val languageState by Kloca.languageStateFlow().collectAsState(
        initial = Kloca.LanguageState(
            Kloca.getLanguageMode(),
            Kloca.getCurrentLanguage(),
        ),
    )

    return remember(key, args, languageState) {
        Kloca.getString(key, *args)
    }
}

/**
 * Extension function for easier access to localized strings.
 * Automatically handles both system language and user preferences.
 *
 * Usage: StringKeys.GREETING_HELLO.localized()
 */
@Composable
fun String.localized(): String = localizedString(this)

/**
 * Extension function for easier access to formatted localized strings.
 * Automatically handles both system language and user preferences.
 *
 * Usage: StringKeys.GREETING_HELLO.localized("John")
 */
@Composable
fun String.localized(vararg args: Any): String = localizedString(this, *args)

/**
 * Composable hook that provides language change notifications.
 * Useful for triggering recomposition when language changes.
 *
 * @return Current language code
 */
@Composable
fun useLanguageChange(): String {
    val currentLanguage by Kloca.currentLanguageFlow().collectAsState(
        initial = Kloca.getCurrentLanguage(),
    )

    return currentLanguage
}

/**
 * Composable hook that provides language mode change notifications.
 *
 * @return Current language mode
 */
@Composable
fun useLanguageModeChange(): Kloca.LanguageMode {
    val currentMode by Kloca.languageModeFlow().collectAsState(
        initial = Kloca.getLanguageMode(),
    )

    return currentMode
}
