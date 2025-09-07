package dev.rlce.kloca.runtime

/**
 * Platform-specific system language provider.
 * Provides access to the current system language/locale.
 */
expect object SystemLanguageProvider {
    /**
     * Gets the current system language code (e.g., "en", "es", "fr").
     * Returns the primary language code without region specifiers.
     */
    fun getCurrentLanguageCode(): String

    /**
     * Gets the full locale identifier (e.g., "en-US", "es-ES", "fr-FR").
     * Useful for region-specific localizations.
     */
    fun getCurrentLocale(): String
}
