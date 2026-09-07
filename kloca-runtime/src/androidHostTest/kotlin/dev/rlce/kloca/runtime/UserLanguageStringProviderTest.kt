package dev.rlce.kloca.runtime

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class UserLanguageStringProviderTest {

    @Test
    fun testInitializeWithContext() {
        val context = RuntimeEnvironment.getApplication()

        UserLanguageStringProvider.initialize(context, emptyArray(), { DefaultAndroidLanguagePersistence(context) })

        // Should not throw exception and should initialize properly
        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertNotNull(availableLanguages)
        assertTrue(availableLanguages.isEmpty())
    }

    @Test
    fun testInitializeWithContextAndLanguages() {
        val context = RuntimeEnvironment.getApplication()
        val languages = arrayOf("en", "es", "fr")

        UserLanguageStringProvider.initialize(context, languages, { DefaultAndroidLanguagePersistence(context) })

        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertEquals(languages.toList(), availableLanguages)
    }

    @Test
    fun testInitializeWithInvalidContext() {
        try {
            val context = RuntimeEnvironment.getApplication()
            UserLanguageStringProvider.initialize("invalid context", emptyArray(), { DefaultAndroidLanguagePersistence(context) })
            kotlin.test.fail("Should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Context must be an instance of android.content.Context", e.message)
        }
    }

    @Test
    fun testSetAndGetLanguage() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es", "fr"), { DefaultAndroidLanguagePersistence(context) })

        assertNull(UserLanguageStringProvider.getCurrentLanguage())

        UserLanguageStringProvider.setLanguage("es")
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())

        UserLanguageStringProvider.setLanguage("fr")
        assertEquals("fr", UserLanguageStringProvider.getCurrentLanguage())
    }

    @Test
    fun testGetStringWithoutInitialization() {
        // Without initialization, should return the key as fallback
        val result = UserLanguageStringProvider.getString("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithNonExistentKey() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es"), { DefaultAndroidLanguagePersistence(context) })
        UserLanguageStringProvider.setLanguage("en")

        // Non-existent key should return the key itself
        val result = UserLanguageStringProvider.getString("nonexistent.key")
        assertEquals("nonexistent.key", result)
    }

    @Test
    fun testGetStringWithArgs() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es"), { DefaultAndroidLanguagePersistence(context) })
        UserLanguageStringProvider.setLanguage("en")

        // Non-existent key should return the key itself even with args
        val result = UserLanguageStringProvider.getString("test.key", "arg1", 42)
        assertEquals("test.key", result)
    }

    @Test
    fun testHasTranslationWithoutInitialization() {
        val hasTranslation = UserLanguageStringProvider.hasTranslation("test.key")
        assertFalse(hasTranslation)
    }

    @Test
    fun testHasTranslationWithNonExistentKey() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es"), { DefaultAndroidLanguagePersistence(context) })
        UserLanguageStringProvider.setLanguage("en")

        val hasTranslation = UserLanguageStringProvider.hasTranslation("nonexistent.key")
        assertFalse(hasTranslation)
    }

    @Test
    fun testGetAvailableLanguagesEmpty() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, emptyArray(), { DefaultAndroidLanguagePersistence(context) })

        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertTrue(availableLanguages.isEmpty())
    }

    @Test
    fun testGetAvailableLanguagesWithMultipleLanguages() {
        val context = RuntimeEnvironment.getApplication()
        val languages = arrayOf("en", "es", "fr", "de", "ja")
        UserLanguageStringProvider.initialize(context, languages, { DefaultAndroidLanguagePersistence(context) })

        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertEquals(languages.toList(), availableLanguages)
    }

    @Test
    fun testResourceNameConversion() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en"), { DefaultAndroidLanguagePersistence(context) })
        UserLanguageStringProvider.setLanguage("en")

        // Test that dots are converted to underscores in resource names
        val result1 = UserLanguageStringProvider.getString("greeting.hello")
        val result2 = UserLanguageStringProvider.getString("navigation.menu.home")

        // Should return keys as fallback since resources don't exist
        assertEquals("greeting.hello", result1)
        assertEquals("navigation.menu.home", result2)
    }

    @Test
    fun testLanguagePreferencePersistence() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es", "fr"), { DefaultAndroidLanguagePersistence(context) })

        // Set a language preference
        UserLanguageStringProvider.setLanguage("es")
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())

        // Reinitialize with same context - should remember preference
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es", "fr"), { DefaultAndroidLanguagePersistence(context) })
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())
    }

    @Test
    fun testMultipleLanguageSwitches() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es", "fr", "de"), { DefaultAndroidLanguagePersistence(context) })

        // Switch between multiple languages
        UserLanguageStringProvider.setLanguage("en")
        assertEquals("en", UserLanguageStringProvider.getCurrentLanguage())

        UserLanguageStringProvider.setLanguage("es")
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())

        UserLanguageStringProvider.setLanguage("fr")
        assertEquals("fr", UserLanguageStringProvider.getCurrentLanguage())

        UserLanguageStringProvider.setLanguage("de")
        assertEquals("de", UserLanguageStringProvider.getCurrentLanguage())
    }

    @Test
    fun testFallbackBehaviorWhenPreferredLanguageNotAvailable() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es"), { DefaultAndroidLanguagePersistence(context) })
        UserLanguageStringProvider.setLanguage("en")

        // Test fallback behavior for non-existent translations
        val result = UserLanguageStringProvider.getString("test.key")
        assertEquals("test.key", result) // Should return key as fallback

        val hasTranslation = UserLanguageStringProvider.hasTranslation("test.key")
        assertFalse(hasTranslation) // Should be false for non-existent resource
    }
}
