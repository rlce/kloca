package dev.rlce.kloca.runtime

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSBundle

/**
 * iOS implementation of StringProvider that reads localized strings from bundle resources.
 * Automatically detects system language changes and invalidates cache.
 */
actual object SystemLanguageStringProvider {

    private val stringCache = mutableMapOf<String, Map<String, String>>()
    private var lastKnownLanguage: String? = null
    private val maxCacheSize = 50 // Limit cache size to prevent memory leaks

    actual fun initialize(context: Any) {
        // No specific initialization needed for iOS
        lastKnownLanguage = getCurrentLanguage()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getStringMap(languageCode: String): Map<String, String> {
        // Check cache size and clear old entries if needed
        if (stringCache.size >= maxCacheSize) {
            clearOldestCacheEntries()
        }

        return stringCache.getOrPut(languageCode) {
            val resultMap = mutableMapOf<String, String>()

            try {
                // Get the main bundle
                val mainBundle = NSBundle.mainBundle()

                // Get bundle for specific language
                val path = mainBundle.pathForResource("$languageCode", "lproj")
                if (path != null) {
                    // Language-specific bundle found, we'll use direct bundle access
                    // for now we return empty map and rely on NSBundle.localizedStringForKey
                    // in the get() method which automatically handles language selection
                }
            } catch (_: Exception) {
                // If there's an error reading from bundle, the map will remain empty
                // and we'll fall back to returning the key itself
            }

            resultMap
        }
    }

    private fun clearOldestCacheEntries() {
        // Remove half the cache entries to manage memory
        val entriesToKeep = maxCacheSize / 2
        val currentEntries = stringCache.toList()
        stringCache.clear()

        // Keep the most recent entries (simple strategy)
        currentEntries.takeLast(entriesToKeep).forEach { (key, value) ->
            stringCache[key] = value
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun get(key: String): String {
        return try {
            checkAndUpdateLanguage()

            // Get current system language
            val currentLanguage = getCurrentLanguage()

            // Try to get from current language using compose-resources bundle
            var result = getStringFromBundle(key, currentLanguage)

            // If not found and current language is not English, try English as fallback
            if (result == null && currentLanguage != "en") {
                result = getStringFromBundle(key, "en")
            }

            // If still not found, return the key itself
            result ?: key
        } catch (_: Exception) {
            // Return key as fallback
            key
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getStringFromBundle(key: String, languageCode: String): String? {
        return try {
            val mainBundle = NSBundle.mainBundle()
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
    actual fun get(key: String, vararg args: Any): String {
        return try {
            checkAndUpdateLanguage()
            // Get the base string first
            val format = get(key)

            KlocaFormatter.format(format, args)
        } catch (_: Exception) {
            key
        }
    }

    actual fun getCurrentLanguage(): String {
        return SystemLanguageProvider.getCurrentLanguageCode()
    }

    actual fun clearCache() {
        stringCache.clear()
        lastKnownLanguage = getCurrentLanguage()
    }

    private fun checkAndUpdateLanguage() {
        val currentLanguage = getCurrentLanguage()
        if (lastKnownLanguage != currentLanguage) {
            clearCache()
        }
    }
}
