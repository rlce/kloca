package dev.rlce.kloca

import java.io.File
import java.io.FileWriter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class YamlProcessorTest {

    @Test
    fun testYamlProcessing() {
        val tempDir = createTempDir()

        // Create test YAML files
        val enFile = File(tempDir, "en.yaml")
        FileWriter(enFile).use { writer ->
            writer.write(
                """
                greeting:
                  hello: "Hello"
                  goodbye: "Goodbye"
                navigation:
                  home: "Home"
                """.trimIndent(),
            )
        }

        val esFile = File(tempDir, "es.yaml")
        FileWriter(esFile).use { writer ->
            writer.write(
                """
                greeting:
                  hello: "Hola"
                  goodbye: "Adiós"
                navigation:
                  home: "Inicio"
                """.trimIndent(),
            )
        }

        val processor = YamlProcessor()
        val result = processor.processYamlFiles(tempDir)

        // Verify languages
        assertEquals(setOf("en", "es"), result.languages)

        // Verify keys
        val expectedKeys = setOf("greeting.hello", "greeting.goodbye", "navigation.home")
        assertEquals(expectedKeys, result.allKeys)

        // Verify entries
        assertEquals(6, result.entries.size) // 3 keys × 2 languages

        // Verify specific entry
        val helloEn = result.entries.find { it.key == "greeting.hello" && it.language == "en" }
        assertEquals("Hello", helloEn?.value)

        val helloEs = result.entries.find { it.key == "greeting.hello" && it.language == "es" }
        assertEquals("Hola", helloEs?.value)

        // Clean up
        tempDir.deleteRecursively()
    }

    @Test
    fun testStringKeysGeneration() {
        val processor = YamlProcessor()
        val keys = setOf("greeting.hello", "navigation.home", "feature_a.settings")

        val stringKeys = processor.generateStringKeysClass(keys, "")

        assertTrue(stringKeys.contains("const val GREETING_HELLO = \"greeting.hello\""))
        assertTrue(stringKeys.contains("const val NAVIGATION_HOME = \"navigation.home\""))
        assertTrue(stringKeys.contains("const val FEATURE_A_SETTINGS = \"feature_a.settings\""))
    }

    @Test
    fun testStringKeysGenerationWithNamespace() {
        val processor = YamlProcessor()
        val keys = setOf("greeting.hello")

        val stringKeys = processor.generateStringKeysClass(keys, "app")

        assertTrue(stringKeys.contains("object AppStringKeys"))
        assertTrue(stringKeys.contains("const val GREETING_HELLO = \"greeting.hello\""))
    }

    @Test
    fun testValidation() {
        val processor = YamlProcessor()
        val entries = listOf(
            YamlProcessor.TranslationEntry("key1", "value1", "en"),
            YamlProcessor.TranslationEntry("key2", "value2", "en"),
            YamlProcessor.TranslationEntry("key1", "valor1", "es"),
            // Missing key2 for es
        )
        val translations = YamlProcessor.ProcessedTranslations(
            entries = entries,
            languages = setOf("en", "es"),
            allKeys = setOf("key1", "key2"),
        )

        val errors = processor.validateTranslations(translations)

        assertEquals(1, errors.size)
        assertTrue(errors[0].contains("Language 'es' is missing keys: key2"))
    }
}
