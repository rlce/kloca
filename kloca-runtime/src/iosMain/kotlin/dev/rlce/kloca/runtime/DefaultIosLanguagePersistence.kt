package dev.rlce.kloca.runtime

import platform.Foundation.NSUserDefaults

/**
 * Default iOS implementation of LanguagePersistence using NSUserDefaults.
 * This class provides a ready-to-use implementation that apps can use directly
 * or extend for custom behavior.
 */
class DefaultIosLanguagePersistence : LanguagePersistence {

    private companion object Companion {
        const val PREF_LANGUAGE_KEY = "kloca_preferred_language"
    }

    private val userDefaults by lazy { NSUserDefaults.standardUserDefaults }

    override fun saveLanguage(languageCode: String) {
        userDefaults.setObject(languageCode, PREF_LANGUAGE_KEY)
        userDefaults.synchronize()
    }

    override fun loadLanguage(): String? {
        return userDefaults.stringForKey(PREF_LANGUAGE_KEY)
    }

    override fun clearLanguage() {
        userDefaults.removeObjectForKey(PREF_LANGUAGE_KEY)
        userDefaults.synchronize()
    }
}
