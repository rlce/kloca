package dev.rlce.kloca.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class UserLanguageStringProviderTest {

    @Test
    fun testInitializeWithContext() {
        val context = Unit

        UserLanguageStringProvider.initialize(context)

        // Should not throw exception and should initialize properly
        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertNotNull(availableLanguages)
    }

    @Test
    fun testInitializeWithContextAndLanguages() {
        val context = Unit
        val languages = arrayOf("en", "es", "fr")

        UserLanguageStringProvider.initialize(context, languages)

        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertEquals(languages.toList(), availableLanguages)
    }

    @Test
    fun testSetAndGetLanguage() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es", "fr"))

        // Initially should be null (or previously saved value)
        UserLanguageStringProvider.setLanguage("es")
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())

        UserLanguageStringProvider.setLanguage("fr")
        assertEquals("fr", UserLanguageStringProvider.getCurrentLanguage())
    }

    @Test
    fun testGetStringWithoutInitialization() {
        // Should return the key as fallback when not initialized
        val result = UserLanguageStringProvider.getString("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithInitialization() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es"))
        UserLanguageStringProvider.setLanguage("en")

        // Should return the key as fallback since no bundle resources in test
        val result = UserLanguageStringProvider.getString("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithArgs() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es"))
        UserLanguageStringProvider.setLanguage("en")

        // Should return the key as fallback since no bundle resources in test
        val result = UserLanguageStringProvider.getString("test.key", "arg1", 42)
        assertEquals("test.key", result)
    }

    @Test
    fun testHasTranslationWithoutInitialization() {
        val hasTranslation = UserLanguageStringProvider.hasTranslation("test.key")
        assertFalse(hasTranslation)
    }

    @Test
    fun testHasTranslationWithInitialization() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es"))
        UserLanguageStringProvider.setLanguage("en")

        val hasTranslation = UserLanguageStringProvider.hasTranslation("test.key")
        assertFalse(hasTranslation) // Should be false since no bundle resources in test
    }

    @Test
    fun testGetAvailableLanguagesEmpty() {
        UserLanguageStringProvider.initialize(Unit, emptyArray())

        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertTrue(availableLanguages.isEmpty())
    }

    @Test
    fun testGetAvailableLanguagesWithMultipleLanguages() {
        val languages = arrayOf("en", "es", "fr", "de", "ja")
        UserLanguageStringProvider.initialize(Unit, languages)

        val availableLanguages = UserLanguageStringProvider.getAvailableLanguages()
        assertEquals(languages.toList(), availableLanguages)
    }

    @Test
    fun testLanguagePreferencePersistence() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es", "fr"))

        // Set a language preference
        UserLanguageStringProvider.setLanguage("es")
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())

        // Reinitialize - should remember preference due to NSUserDefaults persistence
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es", "fr"))
        assertEquals("es", UserLanguageStringProvider.getCurrentLanguage())
    }

    @Test
    fun testMultipleLanguageSwitches() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es", "fr", "de"))

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
    fun testStringFormattingPatterns() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en"))
        UserLanguageStringProvider.setLanguage("en")

        // Test different formatting patterns - should return key as fallback
        val result1 = UserLanguageStringProvider.getString("format.test", "value1", "value2")
        val result2 = UserLanguageStringProvider.getString("another.format", 123, true)

        assertEquals("format.test", result1)
        assertEquals("another.format", result2)
    }

    @Test
    fun testFallbackToEnglish() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es", "fr"))
        UserLanguageStringProvider.setLanguage("fr")

        // Should attempt French first, then fall back to English, then return key
        val result = UserLanguageStringProvider.getString("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testCacheManagement() {
        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es"))

        // Set language and test multiple string requests
        UserLanguageStringProvider.setLanguage("en")

        val result1 = UserLanguageStringProvider.getString("key1")
        val result2 = UserLanguageStringProvider.getString("key2")
        val result3 = UserLanguageStringProvider.getString("key3")

        assertEquals("key1", result1)
        assertEquals("key2", result2)
        assertEquals("key3", result3)

        // Change language should clear cache
        UserLanguageStringProvider.setLanguage("es")

        val result4 = UserLanguageStringProvider.getString("key1")
        assertEquals("key1", result4)
    }

    @Test
    fun testGetCurrentLanguageDefault() {
        // Before setting any language, should be null or previously saved value
        val currentLang1 = UserLanguageStringProvider.getCurrentLanguage()

        UserLanguageStringProvider.initialize(Unit, arrayOf("en", "es"))

        // After initialization without setting language, should be null or previously saved
        val currentLang2 = UserLanguageStringProvider.getCurrentLanguage()

        UserLanguageStringProvider.setLanguage("es")
        val currentLang3 = UserLanguageStringProvider.getCurrentLanguage()
        assertEquals("es", currentLang3)
    }
}
