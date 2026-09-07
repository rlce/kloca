package dev.rlce.kloca.runtime

import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SystemLanguageProviderTest {

    @Test
    fun testGetCurrentLanguageCode() {
        val languageCode = SystemLanguageProvider.getCurrentLanguageCode()

        assertTrue(languageCode.isNotEmpty())
        assertTrue(languageCode.matches(Regex("^[a-z]{2}$")))
        assertEquals(Locale.getDefault().language, languageCode)
    }

    @Test
    fun testGetCurrentLocale() {
        val currentLocale = SystemLanguageProvider.getCurrentLocale()

        assertTrue(currentLocale.isNotEmpty())
        assertTrue(currentLocale.contains(Locale.getDefault().language))
    }

    @Test
    fun testGetCurrentLocaleWithCountry() {
        // Set a locale with country for testing
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)

            val currentLocale = SystemLanguageProvider.getCurrentLocale()

            assertEquals("en-US", currentLocale)
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun testGetCurrentLocaleWithoutCountry() {
        // Set a locale without country for testing
        val originalLocale = Locale.getDefault()
        try {
            Locale.setDefault(Locale("en"))

            val currentLocale = SystemLanguageProvider.getCurrentLocale()

            assertEquals("en", currentLocale)
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun testDifferentLanguageCodes() {
        val originalLocale = Locale.getDefault()
        try {
            // Test English
            Locale.setDefault(Locale.ENGLISH)
            assertEquals("en", SystemLanguageProvider.getCurrentLanguageCode())

            // Test Spanish
            Locale.setDefault(Locale("es"))
            assertEquals("es", SystemLanguageProvider.getCurrentLanguageCode())

            // Test French
            Locale.setDefault(Locale.FRENCH)
            assertEquals("fr", SystemLanguageProvider.getCurrentLanguageCode())

            // Test German
            Locale.setDefault(Locale.GERMAN)
            assertEquals("de", SystemLanguageProvider.getCurrentLanguageCode())
        } finally {
            Locale.setDefault(originalLocale)
        }
    }

    @Test
    fun testRegionalLocales() {
        val originalLocale = Locale.getDefault()
        try {
            // Test US English
            Locale.setDefault(Locale.US)
            assertEquals("en-US", SystemLanguageProvider.getCurrentLocale())

            // Test UK English
            Locale.setDefault(Locale.UK)
            assertEquals("en-GB", SystemLanguageProvider.getCurrentLocale())

            // Test Canada French
            Locale.setDefault(Locale.CANADA_FRENCH)
            assertEquals("fr-CA", SystemLanguageProvider.getCurrentLocale())

            // Test Spain Spanish
            Locale.setDefault(Locale("es", "ES"))
            assertEquals("es-ES", SystemLanguageProvider.getCurrentLocale())
        } finally {
            Locale.setDefault(originalLocale)
        }
    }
}
