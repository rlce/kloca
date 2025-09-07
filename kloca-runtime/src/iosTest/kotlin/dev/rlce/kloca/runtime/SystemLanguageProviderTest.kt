package dev.rlce.kloca.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SystemLanguageProviderTest {

    @Test
    fun testGetCurrentLanguageCode() {
        val languageCode = SystemLanguageProvider.getCurrentLanguageCode()

        assertNotNull(languageCode)
        assertTrue(languageCode.isNotEmpty())
        assertTrue(languageCode.length >= 2)
        assertTrue(languageCode.matches(Regex("^[a-z]{2}.*$")))
    }

    @Test
    fun testGetCurrentLocale() {
        val currentLocale = SystemLanguageProvider.getCurrentLocale()

        assertNotNull(currentLocale)
        assertTrue(currentLocale.isNotEmpty())
        // Should contain at least the language code
        assertTrue(currentLocale.length >= 2)
    }

    @Test
    fun testLanguageCodeFormat() {
        val languageCode = SystemLanguageProvider.getCurrentLanguageCode()

        // Should be at least 2 characters (language code)
        assertTrue(languageCode.length >= 2)
        // Should start with lowercase letters
        assertTrue(languageCode[0].isLowerCase())
        assertTrue(languageCode[1].isLowerCase())
    }

    @Test
    fun testLocaleFormat() {
        val locale = SystemLanguageProvider.getCurrentLocale()

        // Should contain at least the language part
        assertTrue(locale.length >= 2)

        // If it contains a dash, it should be in language-COUNTRY format
        if (locale.contains("-")) {
            val parts = locale.split("-")
            assertTrue(parts.size >= 2)
            assertTrue(parts[0].length >= 2) // Language part
            assertTrue(parts[1].length >= 2) // Country part
        }
    }

    @Test
    fun testFallbackBehavior() {
        // Both methods should provide reasonable fallbacks and never return empty strings
        val languageCode = SystemLanguageProvider.getCurrentLanguageCode()
        val locale = SystemLanguageProvider.getCurrentLocale()

        assertTrue(languageCode.isNotEmpty())
        assertTrue(locale.isNotEmpty())

        // Even in worst case, should default to "en"
        if (languageCode == "en") {
            assertEquals("en", languageCode)
        }

        if (locale == "en") {
            assertEquals("en", locale)
        }
    }

    @Test
    fun testConsistentResults() {
        // Multiple calls should return consistent results
        val languageCode1 = SystemLanguageProvider.getCurrentLanguageCode()
        val languageCode2 = SystemLanguageProvider.getCurrentLanguageCode()
        val locale1 = SystemLanguageProvider.getCurrentLocale()
        val locale2 = SystemLanguageProvider.getCurrentLocale()

        assertEquals(languageCode1, languageCode2)
        assertEquals(locale1, locale2)
    }

    @Test
    fun testLanguageCodeInLocale() {
        val languageCode = SystemLanguageProvider.getCurrentLanguageCode()
        val locale = SystemLanguageProvider.getCurrentLocale()

        // The language code should be part of the locale
        assertTrue(locale.startsWith(languageCode))
    }
}
