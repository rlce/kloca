package dev.rlce.kloca.runtime

import android.content.Context
import android.os.Build
import java.lang.reflect.Field

/**
 * Android implementation of StringProvider using Android string resources.
 * Automatically detects system language changes and uses efficient resource lookup.
 */
actual object SystemLanguageStringProvider {
    private var context: Context? = null
    private var lastKnownLanguage: String? = null
    private val resourceCache = mutableMapOf<String, Int>()
    private var stringClass: Class<*>? = null

    /**
     * Initialize the StringProvider with Android context.
     * This should be called from your Application class.
     */
    actual fun initialize(context: Any) {
        if (context !is Context) {
            throw IllegalArgumentException("Context must be an instance of android.content.Context")
        }
        this.context = context.applicationContext
        initializeResourceReflection()
    }

    private fun initializeResourceReflection() {
        try {
            val packageName = context?.packageName ?: return
            stringClass = Class.forName("$packageName.R\$string")
        } catch (e: Exception) {
            android.util.Log.w("StringProvider", "Could not initialize resource reflection", e)
        }
    }

    private fun getResourceId(key: String): Int? {
        val resourceName = key.replace(".", "_")

        // Check cache first
        resourceCache[resourceName]?.let { return it }

        // Try efficient reflection lookup
        stringClass?.let { clazz ->
            try {
                val field: Field = clazz.getDeclaredField(resourceName)
                val resourceId = field.getInt(null)
                if (resourceId != 0) {
                    resourceCache[resourceName] = resourceId
                    return resourceId
                }
            } catch (e: NoSuchFieldException) {
                // Resource doesn't exist, cache negative result
                resourceCache[resourceName] = 0
                return 0
            } catch (e: Exception) {
                android.util.Log.w("StringProvider", "Error accessing resource field: $resourceName", e)
            }
        }

        // Fallback to getIdentifier (slower but more reliable)
        val context = this.context ?: return 0
        try {
            val resourceId = context.resources.getIdentifier(
                resourceName,
                "string",
                context.packageName,
            )
            resourceCache[resourceName] = resourceId
            return resourceId
        } catch (e: Exception) {
            android.util.Log.w("StringProvider", "Failed to get resource ID for: $resourceName", e)
            return 0
        }
    }

    actual fun get(key: String): String {
        val context = this.context ?: return key
        checkAndUpdateLanguage()

        try {
            val resourceId = getResourceId(key) ?: return key
            if (resourceId != 0) {
                return context.getString(resourceId)
            }
        } catch (e: Exception) {
            android.util.Log.w("StringProvider", "Failed to get string for key: $key", e)
        }

        return key
    }

    actual fun get(key: String, vararg args: Any): String {
        val context = this.context ?: return key
        checkAndUpdateLanguage()

        try {
            val resourceId = getResourceId(key) ?: return key
            if (resourceId != 0) {
                return context.getString(resourceId, *args)
            }
        } catch (e: Exception) {
            android.util.Log.w("StringProvider", "Failed to get formatted string for key: $key", e)
        }

        return key
    }

    actual fun getCurrentLanguage(): String {
        val context = this.context
        return if (context != null) {
            // Use Android context if available for accurate locale detection
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.resources.configuration.locales[0].language
            } else {
                @Suppress("DEPRECATION")
                context.resources.configuration.locale.language
            }
        } else {
            // Fallback to SystemLanguageProvider if context is not initialized
            SystemLanguageProvider.getCurrentLanguageCode()
        }
    }

    actual fun clearCache() {
        // Clear our resource ID cache when language changes
        resourceCache.clear()
        lastKnownLanguage = getCurrentLanguage()
    }

    private fun checkAndUpdateLanguage() {
        val currentLanguage = getCurrentLanguage()
        if (lastKnownLanguage != currentLanguage) {
            clearCache()
        }
    }
}
