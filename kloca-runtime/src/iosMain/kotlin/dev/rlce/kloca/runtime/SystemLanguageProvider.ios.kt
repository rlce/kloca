package dev.rlce.kloca.runtime

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSUserDefaults

/**
 * iOS implementation of SystemLanguageProvider.
 * Uses NSUserDefaults to detect the current system language preferences.
 */
actual object SystemLanguageProvider {

    @OptIn(ExperimentalForeignApi::class)
    actual fun getCurrentLanguageCode(): String {
        return try {
            // Get user's preferred languages from NSUserDefaults
            val userDefaults = NSUserDefaults.standardUserDefaults()
            val languages = userDefaults.objectForKey("AppleLanguages") as? List<*>

            val firstLanguage = languages?.firstOrNull() as? String

            // Extract just the language part (e.g., "en" from "en-US")
            firstLanguage?.split("-")?.firstOrNull() ?: "en"
        } catch (e: Exception) {
            "en"
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun getCurrentLocale(): String {
        return try {
            // Get user's preferred languages from NSUserDefaults
            val userDefaults = NSUserDefaults.standardUserDefaults()
            val languages = userDefaults.objectForKey("AppleLanguages") as? List<*>

            val firstLanguage = languages?.firstOrNull() as? String

            // Convert from iOS format if needed, otherwise return as-is
            firstLanguage?.replace("_", "-") ?: "en"
        } catch (e: Exception) {
            "en"
        }
    }
}
