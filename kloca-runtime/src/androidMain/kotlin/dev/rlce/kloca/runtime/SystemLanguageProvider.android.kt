package dev.rlce.kloca.runtime

import java.util.Locale

/**
 * Android implementation of SystemLanguageProvider.
 * Uses Java Locale to get system language information.
 */
actual object SystemLanguageProvider {

    /**
     * Gets the current system language code (e.g., "en", "es", "fr").
     * Returns the primary language code without region specifiers.
     */
    actual fun getCurrentLanguageCode(): String {
        return Locale.getDefault().language
    }

    /**
     * Gets the full locale identifier (e.g., "en-US", "es-ES", "fr-FR").
     * Useful for region-specific localizations.
     */
    actual fun getCurrentLocale(): String {
        val locale = Locale.getDefault()
        val country = locale.country
        return if (country.isNotEmpty()) {
            "${locale.language}-$country"
        } else {
            locale.language
        }
    }
}
