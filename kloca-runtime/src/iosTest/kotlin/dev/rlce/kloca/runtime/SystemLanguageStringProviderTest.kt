package dev.rlce.kloca.runtime

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SystemLanguageStringProviderTest {

    @Test
    fun testInitialize() {
        val context = Unit

        SystemLanguageStringProvider.initialize(context)

        // Should not throw exception and should work normally
        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }

    @Test
    fun testGetStringWithoutInitialization() {
        // Should return the key as fallback when not initialized properly
        val result = SystemLanguageStringProvider.get("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithInitialization() {
        SystemLanguageStringProvider.initialize(Unit)

        // Should return the key as fallback since no resources are available in test
        val result = SystemLanguageStringProvider.get("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithArgsWithoutInitialization() {
        // Should return the key as fallback
        val result = SystemLanguageStringProvider.get("test.key", "arg1", "arg2")
        assertEquals("test.key", result)
    }

    @Test
    fun testGetStringWithArgs() {
        SystemLanguageStringProvider.initialize(Unit)

        // Should return the key as fallback since no resources are available
        val result = SystemLanguageStringProvider.get("test.key", "arg1", 42, true)
        assertEquals("test.key", result)
    }

    @Test
    fun testGetCurrentLanguage() {
        SystemLanguageStringProvider.initialize(Unit)

        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
        assertTrue(currentLanguage.matches(Regex("^[a-z]{2}.*$")))
    }

    @Test
    fun testGetCurrentLanguageWithoutInitialization() {
        // Should delegate to SystemLanguageProvider
        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }

    @Test
    fun testClearCache() {
        SystemLanguageStringProvider.initialize(Unit)

        // Should not throw exception
        SystemLanguageStringProvider.clearCache()

        // Should still work after clearing cache
        val result = SystemLanguageStringProvider.get("test.key")
        assertEquals("test.key", result)
    }

    @Test
    fun testCacheManagement() {
        SystemLanguageStringProvider.initialize(Unit)

        // Test multiple string requests
        val result1 = SystemLanguageStringProvider.get("key1")
        val result2 = SystemLanguageStringProvider.get("key2")
        val result3 = SystemLanguageStringProvider.get("key3")

        assertEquals("key1", result1)
        assertEquals("key2", result2)
        assertEquals("key3", result3)

        // Clear cache and test again
        SystemLanguageStringProvider.clearCache()

        val result4 = SystemLanguageStringProvider.get("key1")
        assertEquals("key1", result4)
    }

    @Test
    fun testLanguageChangeDetection() {
        SystemLanguageStringProvider.initialize(Unit)

        val initialLanguage = SystemLanguageStringProvider.getCurrentLanguage()

        // Clear cache should update last known language
        SystemLanguageStringProvider.clearCache()

        val afterClearLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertEquals(initialLanguage, afterClearLanguage)
    }

    @Test
    fun testMultipleInitializationCalls() {
        // Should handle multiple initialization calls gracefully
        SystemLanguageStringProvider.initialize(Unit)
        SystemLanguageStringProvider.initialize(Unit)
        SystemLanguageStringProvider.initialize(Unit)

        val currentLanguage = SystemLanguageStringProvider.getCurrentLanguage()
        assertNotNull(currentLanguage)
        assertTrue(currentLanguage.isNotEmpty())
    }

    @Test
    fun testFormattingPatterns() {
        SystemLanguageStringProvider.initialize(Unit)

        // Test different formatting patterns - should all return the key as fallback
        val result1 = SystemLanguageStringProvider.get("format.test", "value1", "value2")
        val result2 = SystemLanguageStringProvider.get("another.format", 123, true)

        assertEquals("format.test", result1)
        assertEquals("another.format", result2)
    }

    @Test
    fun testBundlePathHandling() {
        SystemLanguageStringProvider.initialize(Unit)

        // Test with different key formats
        val dotKey = SystemLanguageStringProvider.get("greeting.hello")
        val underscoreKey = SystemLanguageStringProvider.get("greeting_hello")
        val simpleKey = SystemLanguageStringProvider.get("hello")

        // All should return the key as fallback since no bundle resources in test
        assertEquals("greeting.hello", dotKey)
        assertEquals("greeting_hello", underscoreKey)
        assertEquals("hello", simpleKey)
    }
}
