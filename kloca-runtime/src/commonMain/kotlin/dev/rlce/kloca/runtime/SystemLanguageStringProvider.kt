package dev.rlce.kloca.runtime

/**
 * Platform-specific string provider for accessing localized strings.
 * Automatically detects and responds to system language changes.
 */
expect object SystemLanguageStringProvider {

    /**
     * Initialize the StringProvider with platform-specific context.
     * Android: Pass Application context
     * iOS: Pass Unit or any object (context not needed)
     */
    fun initialize(context: Any)

    /**
     * Get a localized string by key.
     * Returns the key itself as fallback if translation not found.
     */
    fun get(key: String): String

    /**
     * Get a formatted localized string by key with arguments.
     * Uses platform-specific string formatting.
     */
    fun get(key: String, vararg args: Any): String

    /**
     * Get current system language code (e.g., "en", "es", "fr").
     * Returns system language setting.
     */
    fun getCurrentLanguage(): String

    /**
     * Clear internal caches.
     * Useful when language changes or resources are updated.
     */
    fun clearCache()
}
