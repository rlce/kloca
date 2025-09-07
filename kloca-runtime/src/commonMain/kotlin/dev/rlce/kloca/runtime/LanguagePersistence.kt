package dev.rlce.kloca.runtime

/**
 * Platform-agnostic interface for persisting language preferences.
 * Implementations should handle platform-specific persistence mechanisms
 * such as SharedPreferences on Android or NSUserDefaults on iOS.
 */
interface LanguagePersistence {

    /**
     * Save the preferred language code to persistent storage.
     * @param languageCode Language code to save (e.g., "en", "es", "fr")
     */
    fun saveLanguage(languageCode: String)

    /**
     * Load the previously saved language code from persistent storage.
     * @return Saved language code or null if no preference was saved
     */
    fun loadLanguage(): String?

    /**
     * Clear the saved language preference from persistent storage.
     */
    fun clearLanguage()
}
