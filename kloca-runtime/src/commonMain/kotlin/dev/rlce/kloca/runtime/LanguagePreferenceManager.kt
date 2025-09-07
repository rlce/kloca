package dev.rlce.kloca.runtime

/**
 * Helper object for managing language preferences with the CustomLocalizationProvider.
 * Provides a convenient API for language selection and preference management.
 */
object LanguagePreferenceManager {

    /**
     * Set the user's preferred language.
     * @param languageCode Language code (e.g., "en", "es", "fr")
     */
    fun setUserLanguage(languageCode: String) {
        UserLanguageStringProvider.setLanguage(languageCode)
    }

    /**
     * Get the current user's preferred language.
     * @return Current language code or null if using system language
     */
    fun getUserLanguage(): String? {
        return UserLanguageStringProvider.getCurrentLanguage()
    }

    /**
     * Get all available languages.
     * @return List of available language codes
     */
    fun getAvailableLanguages(): List<String> {
        return UserLanguageStringProvider.getAvailableLanguages()
    }
}
