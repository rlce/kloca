package dev.rlce.kloca.runtime

/**
 * Custom localization provider that allows using translations based on user preferences
 * instead of system language settings.
 */
expect object UserLanguageStringProvider {

    /**
     * Initialize the provider with available languages and language persistence factory.
     *
     * @param context Platform-specific context (e.g., Android Context, iOS Bundle)
     * @param availableLanguages List of language codes that have translations available
     * @param languagePersistenceFactory Factory function that creates platform-specific language persistence instance.
     *                                   Used to save/load user's preferred language across app restarts.
     *                                   For Android: Use DefaultAndroidLanguagePersistence(context)
     *                                   For iOS: Use DefaultIosLanguagePersistence()
     */
    fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory)

    /**
     * Set the preferred language for translations.
     * @param languageCode Language code (e.g., "en", "es", "fr")
     */
    fun setLanguage(languageCode: String)

    /**
     * Get the current preferred language code.
     * @return Current language code or null if not set (falls back to system language)
     */
    fun getCurrentLanguage(): String?

    /**
     * Get a localized string for the given key using the preferred language.
     * Falls back to default language if translation not found.
     * @param key Translation key
     * @return Localized string or key if translation not found
     */
    fun getString(key: String): String

    /**
     * Get a localized string with format arguments using the preferred language.
     * Falls back to default language if translation not found.
     * @param key Translation key
     * @param args Format arguments
     * @return Formatted localized string or key if translation not found
     */
    fun getString(key: String, vararg args: Any): String

    /**
     * Check if a translation exists for the given key in the current language.
     * @param key Translation key
     * @return true if translation exists, false otherwise
     */
    fun hasTranslation(key: String): Boolean

    /**
     * Get list of available language codes.
     * @return List of language codes that have translations available
     */
    fun getAvailableLanguages(): List<String>
}
