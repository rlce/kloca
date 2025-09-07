package dev.rlce.kloca.runtime

import android.content.Context
import androidx.core.content.edit

/**
 * Default Android implementation of LanguagePersistence using SharedPreferences.
 * This class provides a ready-to-use implementation that apps can use directly
 * or extend for custom behavior.
 */
class DefaultAndroidLanguagePersistence(private val context: Context) : LanguagePersistence {

    private companion object Companion {
        const val PREF_NAME = "kloca_localization"
        const val PREF_LANGUAGE_KEY = "preferred_language"
    }

    private val preferences by lazy {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun saveLanguage(languageCode: String) {
        preferences.edit {
            putString(PREF_LANGUAGE_KEY, languageCode)
        }
    }

    override fun loadLanguage(): String? {
        return preferences.getString(PREF_LANGUAGE_KEY, null)
    }

    override fun clearLanguage() {
        preferences.edit {
            remove(PREF_LANGUAGE_KEY)
        }
    }
}
