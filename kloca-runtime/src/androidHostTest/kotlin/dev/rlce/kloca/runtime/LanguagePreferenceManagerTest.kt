package dev.rlce.kloca.runtime

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class LanguagePreferenceManagerTest {

    @Test
    fun testSetUserLanguage() {
        val context = RuntimeEnvironment.getApplication()
        // Initialize UserLanguageStringProvider first (needed for LanguagePreferenceManager to work)
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es", "fr"), { DefaultAndroidLanguagePersistence(context) })

        LanguagePreferenceManager.setUserLanguage("es")

        assertEquals("es", LanguagePreferenceManager.getUserLanguage())
    }

    @Test
    fun testGetUserLanguage() {
        val context = RuntimeEnvironment.getApplication()
        // Initialize UserLanguageStringProvider first
        UserLanguageStringProvider.initialize(context, arrayOf("en", "es", "fr"), { DefaultAndroidLanguagePersistence(context) })

        LanguagePreferenceManager.setUserLanguage("fr")
        val userLanguage = LanguagePreferenceManager.getUserLanguage()

        assertEquals("fr", userLanguage)
    }

    @Test
    fun testGetAvailableLanguages() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr", "de")
        UserLanguageStringProvider.initialize(context, availableLanguages, { DefaultAndroidLanguagePersistence(context) })

        val languages = LanguagePreferenceManager.getAvailableLanguages()

        assertNotNull(languages)
        assertEquals(availableLanguages.toList(), languages)
    }

    @Test
    fun testSetAndGetDifferentLanguages() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr", "de", "ja")
        UserLanguageStringProvider.initialize(context, availableLanguages, { DefaultAndroidLanguagePersistence(context) })

        LanguagePreferenceManager.setUserLanguage("ja")
        assertEquals("ja", LanguagePreferenceManager.getUserLanguage())

        LanguagePreferenceManager.setUserLanguage("de")
        assertEquals("de", LanguagePreferenceManager.getUserLanguage())
    }

    @Test
    fun testGetAvailableLanguagesEmptyByDefault() {
        val context = RuntimeEnvironment.getApplication()
        UserLanguageStringProvider.initialize(context, emptyArray(), { DefaultAndroidLanguagePersistence(context) })

        val languages = LanguagePreferenceManager.getAvailableLanguages()

        assertNotNull(languages)
        assertEquals(emptyList(), languages)
    }
}
