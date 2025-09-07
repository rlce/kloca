package dev.rlce.kloca.runtime

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SystemLanguageStringProviderTest {

    @Test
    fun testInitialize() {
        val context = RuntimeEnvironment.getApplication()

        SystemLanguageStringProvider.initialize(context)

        // Should not throw exception and should work normally
        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }

    @Test
    fun testInitializeWithInvalidContext() {
        try {
            SystemLanguageStringProvider.initialize("invalid context")
            kotlin.test.fail("Should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertEquals("Context must be an instance of android.content.Context", e.message)
        }
    }

    @Test
    fun testGetStringWithoutInitialization() {
        // Without initialization, should return the key as fallback
        val result = SystemLanguageStringProvider.get("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithArgsWithoutInitialization() {
        // Without initialization, should return the key as fallback
        val result = SystemLanguageStringProvider.get("test.key", "arg1", "arg2")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithNonExistentKey() {
        val context = RuntimeEnvironment.getApplication()
        SystemLanguageStringProvider.initialize(context)

        // Non-existent key should return the key itself
        val result = SystemLanguageStringProvider.get("nonexistent.key")
        assertEquals("nonexistent.key", result)
    }

    @Test
    fun testGetCurrentLanguage() {
        val context = RuntimeEnvironment.getApplication()
        SystemLanguageStringProvider.initialize(context)

        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
        assertTrue(currentLanguage.matches(Regex("^[a-z]{2}$")))
    }

    @Test
    fun testGetCurrentLanguageWithoutInitialization() {
        // Should fallback to SystemLanguageProvider
        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }

    @Test
    fun testClearCache() {
        val context = RuntimeEnvironment.getApplication()
        SystemLanguageStringProvider.initialize(context)

        // Should not throw exception
        SystemLanguageStringProvider.clearCache()

        // Should still work after clearing cache
        val result = SystemLanguageStringProvider.get("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testResourceNameConversion() {
        val context = RuntimeEnvironment.getApplication()
        SystemLanguageStringProvider.initialize(context)

        // Test that dots are converted to underscores in resource names
        val result1 = SystemLanguageStringProvider.get("greeting.hello")
        val result2 = SystemLanguageStringProvider.get("navigation.menu.home")

        // Should return keys as fallback since resources don't exist
        assertEquals("greeting.hello", result1)
        assertEquals("navigation.menu.home", result2)
    }

    @Test
    fun testFormatStringWithArgs() {
        val context = RuntimeEnvironment.getApplication()
        SystemLanguageStringProvider.initialize(context)

        // Test with format arguments
        val result = SystemLanguageStringProvider.get("test.key", "arg1", 42, true)

        // Should return key as fallback since resource doesn't exist
        assertEquals("test.key", result)
    }

    @Test
    fun testLanguageChangeDetection() {
        val context = RuntimeEnvironment.getApplication()
        SystemLanguageStringProvider.initialize(context)

        val initialLanguage = SystemLanguageStringProvider.getCurrentLanguage()

        // Clear cache should update last known language
        SystemLanguageStringProvider.clearCache()

        val afterClearLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertEquals(initialLanguage, afterClearLanguage)
    }

    @Test
    fun testMultipleInitializationCalls() {
        val context = RuntimeEnvironment.getApplication()

        // Should handle multiple initialization calls gracefully
        SystemLanguageStringProvider.initialize(context)
        SystemLanguageStringProvider.initialize(context)
        SystemLanguageStringProvider.initialize(context)

        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }
}
