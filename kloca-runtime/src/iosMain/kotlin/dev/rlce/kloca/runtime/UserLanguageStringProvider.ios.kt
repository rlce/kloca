package dev.rlce.kloca.runtime

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of CustomLocalizationProvider that allows setting user-preferred language
 * independent of system language settings.
 */
actual object UserLanguageStringProvider {
    private const val PREF_LANGUAGE_KEY = "kloca_preferred_language"

    private var preferredLanguage: String? = null
    private val stringCache = mutableMapOf<String, Map<String, String>>()
    private var availableLanguages: List<String> = emptyList()
    private val maxCacheSize = 50 // Limit cache size to prevent memory leaks

    init {
        loadSavedLanguage()
    }

    actual fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory) {
        this.availableLanguages = availableLanguages.toList()
    }

    private fun loadSavedLanguage() {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val savedLanguage = userDefaults.stringForKey(PREF_LANGUAGE_KEY)
        if (savedLanguage != null) {
            preferredLanguage = savedLanguage
        }
    }

    actual fun setLanguage(languageCode: String) {
        preferredLanguage = languageCode
        val userDefaults = NSUserDefaults.standardUserDefaults
        userDefaults.setObject(languageCode, PREF_LANGUAGE_KEY)
        userDefaults.synchronize()
        // Clear cache when language changes to free memory
        clearCache()
    }

    private fun manageCacheSize() {
        if (stringCache.size >= maxCacheSize) {
            // Remove half the cache entries to manage memory
            val entriesToKeep = maxCacheSize / 2
            val currentEntries = stringCache.toList()
            stringCache.clear()

            // Keep the most recent entries (simple strategy)
            currentEntries.takeLast(entriesToKeep).forEach { (key, value) ->
                stringCache[key] = value
            }
        }
    }

    private fun clearCache() {
        stringCache.clear()
    }

    actual fun getCurrentLanguage(): String? = preferredLanguage

    @OptIn(ExperimentalForeignApi::class)
    actual fun getString(key: String): String {
        return try {
            val currentLanguage = preferredLanguage ?: "en"

            // Try to get from preferred language using compose-resources bundle
            var result = getStringFromComposeBundle(key, currentLanguage)

            // If not found and preferred language is not English, try English
            if (result == null && currentLanguage != "en") {
                result = getStringFromComposeBundle(key, "en")
            }

            // If still not found, return the key itself
            result ?: key
        } catch (_: Exception) {
            key
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getStringFromComposeBundle(key: String, languageCode: String): String? {
        return try {
            val mainBundle = NSBundle.mainBundle
            val bundlePath = mainBundle.bundlePath

            // Try to get the compose-resources bundle
            val composeResourcesPath = "$bundlePath/compose-resources"
            val composeBundle = NSBundle.bundleWithPath(composeResourcesPath)

            if (composeBundle != null) {
                // Try to get language-specific bundle from compose-resources
                val langPath = composeBundle.pathForResource("$languageCode", "lproj")
                val langBundle = if (langPath != null) {
                    NSBundle.bundleWithPath(langPath)
                } else {
                    null
                }

                if (langBundle != null) {
                    val localizedString = langBundle.localizedStringForKey(key, value = null, table = null)
                    if (localizedString != null && localizedString != key) {
                        return localizedString
                    }
                }
            }

            null
        } catch (_: Exception) {
            null
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getString(key: String, vararg args: Any): String {
        return try {
            // Get the base string first
            val format = getString(key)

            // Convert args to string format
            val stringArgs = args.map { it.toString() }

            // Use string replacement for formatting - handle multiple format patterns
            var result = format
            stringArgs.forEachIndexed { index, arg ->
                val argIndex = index + 1
                // Handle Android/Java style formatting: %1$s, %2$s, etc.
                result = result.replace("%${argIndex}\$s", arg)
                // Handle iOS style formatting: %1$@, %2$@, etc.
                result = result.replace("%${argIndex}\$@", arg)
                // Handle simple positional formatting: {0}, {1}, etc.
                result = result.replace("{$index}", arg)
                // Handle simple %s formatting (sequential)
                if (index == 0) {
                    result = result.replaceFirst("%s", arg)
                }
            }
            result
        } catch (_: Exception) {
            key
        }
    }

    actual fun hasTranslation(key: String): Boolean {
        return try {
            val currentLanguage = preferredLanguage ?: "en"
            val currentLangMap = getStringMap(currentLanguage)
            currentLangMap.containsKey(key)
        } catch (_: Exception) {
            false
        }
    }

    actual fun getAvailableLanguages(): List<String> {
        return availableLanguages
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getStringMap(languageCode: String): Map<String, String> {
        return stringCache.getOrPut(languageCode) {
            val resultMap = mutableMapOf<String, String>()

            try {
                val bundle = NSBundle.mainBundle
                val bundlePath = bundle.bundlePath

                // Look for the .strings file in compose-resources
                val localizableStringsPath = "$bundlePath/compose-resources/$languageCode.lproj/Localizable.strings"

                // Try to read the .strings file using NSBundle
                val langBundle = NSBundle.bundleWithPath("$bundlePath/compose-resources")
                if (langBundle != null) {
                    // Try to load the specific language bundle
                    val langPath = langBundle.pathForResource("$languageCode", "lproj")
                    val specificBundle = if (langPath != null) {
                        NSBundle.bundleWithPath(langPath)
                    } else {
                        null
                    }

                    if (specificBundle != null) {
                        // Use NSBundle's built-in localization support
                        // This approach relies on NSBundle.localizedStringForKey to do the heavy lifting
                        // We'll leave the map empty and let StringProvider.get() handle the actual lookup
                    }
                }
            } catch (_: Exception) {
                // Cache will remain empty on error
            }

            resultMap
        }
    }
}
