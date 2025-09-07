package dev.rlce.kloca.runtime

/**
 * Factory interface for creating platform-specific LanguagePersistence implementations.
 * Target applications must provide an implementation of this factory to enable
 * language persistence functionality.
 */
fun interface LanguagePersistenceFactory {

    /**
     * Create a LanguagePersistence implementation for the current platform.
     * @param context Platform-specific context (e.g., Android Context, iOS Bundle)
     * @return LanguagePersistence implementation
     */
    fun createLanguagePersistence(context: Any): LanguagePersistence
}
