package dev.rlce.kloca.runtime

import kotlinx.coroutines.test.runTest
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class KlocaSimpleTest {

    @AfterTest
    fun tearDown() {
        Kloca.reset()
    }

    @Test
    fun testInitializeWithContext() {
        val context = RuntimeEnvironment.getApplication()

        Kloca.initialize(context) {
            DefaultAndroidLanguagePersistence(context)
        }

        assertTrue(Kloca.isReady())
        assertEquals(Kloca.LanguageMode.SYSTEM, Kloca.getLanguageMode())
    }

    @Test
    fun testInitializeWithContextAndLanguages() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr")

        Kloca.initialize(context, availableLanguages) {
            DefaultAndroidLanguagePersistence(context)
        }

        assertTrue(Kloca.isReady())
        assertEquals(Kloca.LanguageMode.USER_PREFERENCE, Kloca.getLanguageMode())
    }

    @Test
    fun testGetStringWhenNotInitialized() {
        // Should return key as fallback when not properly initialized
        val result = Kloca.getString("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testSetUserLanguage() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr")

        Kloca.initialize(context, availableLanguages) {
            DefaultAndroidLanguagePersistence(context)
        }
        Kloca.setUserLanguage("es")

        assertEquals(Kloca.LanguageMode.USER_PREFERENCE, Kloca.getLanguageMode())
        assertTrue(Kloca.isUsingUserPreference())
        assertFalse(Kloca.isUsingSystemLanguage())
    }

    @Test
    fun testUseSystemLanguage() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr")

        Kloca.initialize(context, availableLanguages) {
            DefaultAndroidLanguagePersistence(context)
        }
        Kloca.setUserLanguage("es")
        Kloca.useSystemLanguage()

        assertEquals(Kloca.LanguageMode.SYSTEM, Kloca.getLanguageMode())
        assertTrue(Kloca.isUsingSystemLanguage())
        assertFalse(Kloca.isUsingUserPreference())
    }

    @Test
    fun testStartKlocaConvenienceFunction() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr")

        startKloca(
            context = context,
            availableLanguages = availableLanguages,
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(context) },
        )

        assertTrue(Kloca.isReady())
        assertEquals(Kloca.LanguageMode.USER_PREFERENCE, Kloca.getLanguageMode())
    }

    @Test
    fun testLanguageStateDataClass() {
        val languageState = Kloca.LanguageState(
            mode = Kloca.LanguageMode.SYSTEM,
            currentLanguage = "en",
        )

        assertEquals(Kloca.LanguageMode.SYSTEM, languageState.mode)
        assertEquals("en", languageState.currentLanguage)
    }

    @Test
    fun testLanguageModeEnum() {
        assertEquals("SYSTEM", Kloca.LanguageMode.SYSTEM.name)
        assertEquals("USER_PREFERENCE", Kloca.LanguageMode.USER_PREFERENCE.name)
    }

    @Test
    fun testInitializeOnlyOnce() {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es")

        Kloca.initialize(context, availableLanguages) {
            DefaultAndroidLanguagePersistence(context)
        }
        val isReadyFirst = Kloca.isReady()

        // Initialize again
        Kloca.initialize(context, arrayOf("fr", "de")) {
            DefaultAndroidLanguagePersistence(context)
        }
        val isReadySecond = Kloca.isReady()

        assertEquals(isReadyFirst, isReadySecond)
        assertTrue(Kloca.isReady())
    }

    @Test
    fun testGetStringWithFormatArgs() {
        val context = RuntimeEnvironment.getApplication()

        Kloca.initialize(context) {
            DefaultAndroidLanguagePersistence(context)
        }

        val result = Kloca.getString("test.key", "arg1", "arg2")

        // Should return key as fallback when not properly initialized with providers
        assertEquals("test.key", result)
    }

    @Test
    fun testGetCurrentLanguage() {
        val context = RuntimeEnvironment.getApplication()

        Kloca.initialize(context) {
            DefaultAndroidLanguagePersistence(context)
        }
        val currentLanguage = Kloca.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }

    @Test
    fun testGetAvailableLanguages() = runTest {
        val context = RuntimeEnvironment.getApplication()
        val availableLanguages = arrayOf("en", "es", "fr")

        Kloca.initialize(context, availableLanguages) {
            DefaultAndroidLanguagePersistence(context)
        }
        val languages = Kloca.getAvailableLanguages()
        assertNotNull(languages)
        assertEquals(availableLanguages.toList(), languages)
    }

    @Test
    fun testHasTranslation() {
        val context = RuntimeEnvironment.getApplication()

        Kloca.initialize(context) {
            DefaultAndroidLanguagePersistence(context)
        }

        val hasTranslation = Kloca.hasTranslation("test.key")
        // Should return false since no actual translations are available in test
        assertFalse(hasTranslation)
    }
}
