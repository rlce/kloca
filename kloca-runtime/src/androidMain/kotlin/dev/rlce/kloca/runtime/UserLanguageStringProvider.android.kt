package dev.rlce.kloca.runtime

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import java.util.Locale

/**
 * Android implementation of CustomLocalizationProvider that allows setting user-preferred language
 * independent of system language settings.
 */
actual object UserLanguageStringProvider {
    private const val PREF_NAME = "kloca_localization"
    private const val PREF_LANGUAGE_KEY = "preferred_language"
    private const val TAG = "CustomLocalizationProvider"

    private var context: Context? = null
    private var preferences: SharedPreferences? = null
    private var preferredLanguage: String? = null
    private val resourcesCache = mutableMapOf<String, Resources>()
    private var availableLanguages: List<String> = emptyList()

    actual fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory) {
        if (context is Context) {
            this.context = context.applicationContext
            this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            this.preferredLanguage = preferences?.getString(PREF_LANGUAGE_KEY, null)
        } else {
            throw IllegalArgumentException("Context must be an instance of android.content.Context")
        }
        this.availableLanguages = availableLanguages.toList()
    }

    actual fun setLanguage(languageCode: String) {
        preferredLanguage = languageCode
        preferences?.edit()?.putString(PREF_LANGUAGE_KEY, languageCode)?.apply()
        // Clear cache when language changes
        resourcesCache.clear()
    }

    actual fun getCurrentLanguage(): String? = preferredLanguage

    actual fun getString(key: String): String {
        val context = this.context ?: return key

        try {
            val resources = getResourcesForLanguage(context, preferredLanguage)
            val resourceName = key.replace(".", "_")
            val resourceId = resources.getIdentifier(
                resourceName,
                "string",
                context.packageName,
            )

            if (resourceId != 0) {
                return resources.getString(resourceId)
            }

            // Fallback to default language if preferred language doesn't have the translation
            if (preferredLanguage != null) {
                val defaultResources = getResourcesForLanguage(context, null)
                val defaultResourceId = defaultResources.getIdentifier(
                    resourceName,
                    "string",
                    context.packageName,
                )
                if (defaultResourceId != 0) {
                    return defaultResources.getString(defaultResourceId)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get string for key: $key", e)
        }

        return key
    }

    actual fun getString(key: String, vararg args: Any): String {
        val context = this.context ?: return key

        try {
            val resources = getResourcesForLanguage(context, preferredLanguage)
            val resourceName = key.replace(".", "_")
            val resourceId = resources.getIdentifier(
                resourceName,
                "string",
                context.packageName,
            )

            if (resourceId != 0) {
                return resources.getString(resourceId, *args)
            }

            // Fallback to default language
            if (preferredLanguage != null) {
                val defaultResources = getResourcesForLanguage(context, null)
                val defaultResourceId = defaultResources.getIdentifier(
                    resourceName,
                    "string",
                    context.packageName,
                )
                if (defaultResourceId != 0) {
                    return defaultResources.getString(defaultResourceId, *args)
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get formatted string for key: $key", e)
        }

        return key
    }

    actual fun hasTranslation(key: String): Boolean {
        val context = this.context ?: return false

        return try {
            val resources = getResourcesForLanguage(context, preferredLanguage)
            val resourceName = key.replace(".", "_")
            val resourceId = resources.getIdentifier(
                resourceName,
                "string",
                context.packageName,
            )
            resourceId != 0
        } catch (e: Exception) {
            false
        }
    }

    actual fun getAvailableLanguages(): List<String> {
        return availableLanguages
    }

    private fun getResourcesForLanguage(context: Context, languageCode: String?): Resources {
        val cacheKey = languageCode ?: "default"

        return resourcesCache.getOrPut(cacheKey) {
            if (languageCode != null) {
                val locale = Locale(languageCode)
                val configuration = Configuration(context.resources.configuration)
                configuration.setLocale(locale)
                context.createConfigurationContext(configuration).resources
            } else {
                context.resources
            }
        }
    }
}
