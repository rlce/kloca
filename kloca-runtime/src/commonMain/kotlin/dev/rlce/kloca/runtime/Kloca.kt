package dev.rlce.kloca.runtime

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

object Kloca {

    private var isInitialized = false
    private val _currentMode = MutableStateFlow(LanguageMode.SYSTEM)
    private val _userLanguage = MutableStateFlow<String?>(null)
    private var currentMode: LanguageMode
        get() = _currentMode.value
        set(value) { _currentMode.value = value }

    /**
     * Initialize Kloca with platform-specific context (system language only).
     *
     * @param context Platform-specific context (Application on Android, Unit on iOS)
     * @param languagePersistenceFactory Factory to create LanguagePersistence instance for user language storage
     */
    fun initialize(context: Any, languagePersistenceFactory: LanguagePersistenceFactory) {
        val noUserLanguages = emptyArray<String>()
        initialize(context, noUserLanguages, languagePersistenceFactory)
    }

    /**
     * Initialize Kloca with platform-specific context.
     * This sets up both system language and user language providers.
     *
     * @param context Platform-specific context (Application on Android, Unit on iOS)
     * @param availableLanguages List of languages available for user selection (optional)
     * @param languagePersistenceFactory Factory to create LanguagePersistence instance for user language storage
     */
    fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory) {
        if (isInitialized) {
            return
        }

        // Always initialize both providers for maximum flexibility
        SystemLanguageStringProvider.initialize(context)
        UserLanguageStringProvider.initialize(context, availableLanguages, languagePersistenceFactory)

        // Determine initial language mode and set current language
        val preferredUserLanguage = UserLanguageStringProvider.getCurrentLanguage()
        when {
            preferredUserLanguage != null -> {
                // If user had a preferred language, use it
                currentMode = LanguageMode.USER_PREFERENCE
                _userLanguage.value = preferredUserLanguage
            }

            availableLanguages.size > 0 -> {
                // If userLanguages were provided but no preference set, default to first available
                currentMode = LanguageMode.USER_PREFERENCE
                _userLanguage.value = availableLanguages[0]
            }

            else -> {
                // Otherwise, use system language
                currentMode = LanguageMode.SYSTEM
                _userLanguage.value = null
            }
        }

        isInitialized = true
    }

    /**
     * Get a localized string using the current language mode.
     * @param key Translation key
     * @return Localized string or key if translation not found
     * @throws IllegalStateException if Kloca is not initialized
     */
    fun getString(key: String): String {
        if (!isInitialized) {
            return key // Return key as fallback when not initialized
        }
        return when (currentMode) {
            LanguageMode.SYSTEM -> SystemLanguageStringProvider.get(key)
            LanguageMode.USER_PREFERENCE -> UserLanguageStringProvider.getString(key)
        }
    }

    /**
     * Get a localized string with format arguments using the current language mode.
     * @param key Translation key
     * @param args Format arguments
     * @return Formatted localized string or key if translation not found
     * @throws IllegalStateException if Kloca is not initialized
     */
    fun getString(key: String, vararg args: Any): String {
        if (!isInitialized) {
            return key // Return key as fallback when not initialized
        }
        return when (currentMode) {
            LanguageMode.SYSTEM -> SystemLanguageStringProvider.get(key, *args)
            LanguageMode.USER_PREFERENCE -> UserLanguageStringProvider.getString(key, *args)
        }
    }

    /**
     * Get the currently active language code.
     * @return Current language code based on the active mode
     */
    fun getCurrentLanguage(): String {
        return when (currentMode) {
            LanguageMode.SYSTEM -> SystemLanguageStringProvider.getCurrentLanguage()
            LanguageMode.USER_PREFERENCE -> {
                UserLanguageStringProvider.getCurrentLanguage()
                    ?: SystemLanguageStringProvider.getCurrentLanguage()
            }
        }
    }

    /**
     * Check if a translation exists for the given key in the current language.
     * @param key Translation key
     * @return true if translation exists, false otherwise
     */
    fun hasTranslation(key: String): Boolean {
        return when (currentMode) {
            LanguageMode.SYSTEM -> {
                val result = SystemLanguageStringProvider.get(key)
                result != key
            }
            LanguageMode.USER_PREFERENCE -> UserLanguageStringProvider.hasTranslation(key)
        }
    }

    /**
     * Set user preferred language and switch to USER_PREFERENCE mode.
     * @param languageCode Language code (e.g., "en", "es", "fr")
     */
    fun setUserLanguage(languageCode: String) {
        if (!isInitialized) {
            return
        }
        currentMode = LanguageMode.USER_PREFERENCE
        UserLanguageStringProvider.setLanguage(languageCode)
        _userLanguage.value = languageCode
    }

    /**
     * Switch back to system language mode.
     */
    fun useSystemLanguage() {
        if (!isInitialized) {
            return
        }
        currentMode = LanguageMode.SYSTEM
        _userLanguage.value = null
    }

    /**
     * Get current language preference mode.
     */
    fun getLanguageMode(): LanguageMode = currentMode

    /**
     * Get list of available language codes.
     * @return List of language codes that have translations available
     */
    fun getAvailableLanguages(): List<String> {
        return UserLanguageStringProvider.getAvailableLanguages()
    }

    /**
     * Check if currently using system language mode.
     * @return true if using system language, false if using user preference
     */
    fun isUsingSystemLanguage(): Boolean = currentMode == LanguageMode.SYSTEM

    /**
     * Check if currently using user preference language mode.
     * @return true if using user preference, false if using system language
     */
    fun isUsingUserPreference(): Boolean = currentMode == LanguageMode.USER_PREFERENCE

    /**
     * Check if Kloca has been initialized.
     * @return true if initialized, false otherwise
     */
    fun isReady(): Boolean = isInitialized

    // ========== Flow-based Reactive API ==========

    /**
     * Flow that emits language mode changes.
     * @return Flow of LanguageMode changes
     */
    fun languageModeFlow(): Flow<LanguageMode> = _currentMode.asStateFlow()

    /**
     * Flow that emits current language changes.
     * Combines both system language changes and user preference changes.
     * @return Flow of language code changes
     */
    fun currentLanguageFlow(): Flow<String> = combine(
        _currentMode.asStateFlow(),
        _userLanguage.asStateFlow(),
    ) { _, _ -> getCurrentLanguage() }
        .distinctUntilChanged()

    /**
     * Flow that combines language mode and current language into a data class.
     * Useful when you need to react to both mode and language changes.
     * @return Flow of LanguageState changes
     */
    fun languageStateFlow(): Flow<LanguageState> = combine(
        _currentMode.asStateFlow(),
        _userLanguage.asStateFlow(),
    ) { mode, _ -> LanguageState(mode, getCurrentLanguage()) }
        .distinctUntilChanged()

    /**
     * Reset Kloca to uninitialized state.
     */
    fun reset() {
        isInitialized = false
        _userLanguage.value = null
        _currentMode.value = LanguageMode.SYSTEM
        SystemLanguageStringProvider.clearCache()
        UserLanguageStringProvider.setLanguage("")
    }

    /**
     * Data class representing the complete language state.
     */
    data class LanguageState(
        val mode: LanguageMode,
        val currentLanguage: String,
    )

    /**
     * Current language preference mode.
     */
    enum class LanguageMode {
        /** Use system language (default) */
        SYSTEM,

        /** Use user-preferred language */
        USER_PREFERENCE,
    }
}

/**
 * Convenience function to initialize Kloca with user language selection support.
 * Equivalent to Kloca.initialize(context, availableLanguages, languagePersistenceFactory).
 *
 * @param context Platform-specific context (Application on Android, Unit on iOS)
 * @param availableLanguages List of languages available for user selection
 * @param languagePersistenceFactory Factory function that creates platform-specific language persistence instance.
 *                                   Used to save/load user's preferred language across app restarts.
 *                                   For Android: Use { DefaultAndroidLanguagePersistence(context) }
 *                                   For iOS: Use { DefaultIosLanguagePersistence() }
 */
fun startKloca(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory) {
    Kloca.initialize(context, availableLanguages, languagePersistenceFactory)
}
