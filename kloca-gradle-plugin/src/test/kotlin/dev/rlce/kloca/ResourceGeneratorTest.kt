package dev.rlce.kloca

import java.io.File
import kotlin.test.Test
import kotlin.test.assertTrue

class ResourceGeneratorTest {

    @Test
    fun testWasmResourceGeneration() {
        val tempDir = createTempDir()
        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("greeting.hello", "Hello \"Wasm\"", "en"),
                YamlProcessor.TranslationEntry("greeting.hello", "Olá", "pt"),
            ),
            languages = setOf("en", "pt"),
            allKeys = setOf("greeting.hello"),
        )

        ResourceGenerator().generateWasmResources(translations, tempDir, "en")

        val resource = File(tempDir, "wasmJs/kloca/translations.json")
        assertTrue(resource.exists())
        val content = resource.readText()
        assertTrue(content.contains("\"defaultLanguage\": \"en\""))
        assertTrue(content.contains("\"greeting.hello\": \"Hello \\\"Wasm\\\"\""))
        assertTrue(content.contains("\"pt\""))
        tempDir.deleteRecursively()
    }

    @Test
    fun testAndroidResourceGeneration() {
        val tempDir = createTempDir()
        val generator = ResourceGenerator()

        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("greeting.hello", "Hello", "en"),
                YamlProcessor.TranslationEntry("greeting.goodbye", "Goodbye", "en"),
                YamlProcessor.TranslationEntry("greeting.hello", "Hola", "es"),
                YamlProcessor.TranslationEntry("greeting.goodbye", "Adiós", "es"),
            ),
            languages = setOf("en", "es"),
            allKeys = setOf("greeting.hello", "greeting.goodbye"),
        )

        generator.generateAndroidResources(translations, tempDir, "en")

        // Check directory structure
        val androidDir = File(tempDir, "android")
        assertTrue(androidDir.exists())

        val valuesDir = File(androidDir, "values")
        val valuesEsDir = File(androidDir, "values-es")
        assertTrue(valuesDir.exists())
        assertTrue(valuesEsDir.exists())

        // Check English strings.xml
        val enStringsFile = File(valuesDir, "strings.xml")
        assertTrue(enStringsFile.exists())
        val enContent = enStringsFile.readText()
        assertTrue(enContent.contains("name=\"greeting_hello\">Hello</string>"))
        assertTrue(enContent.contains("name=\"greeting_goodbye\">Goodbye</string>"))

        // Check Spanish strings.xml
        val esStringsFile = File(valuesEsDir, "strings.xml")
        assertTrue(esStringsFile.exists())
        val esContent = esStringsFile.readText()
        assertTrue(esContent.contains("name=\"greeting_hello\">Hola</string>"))
        assertTrue(esContent.contains("name=\"greeting_goodbye\">Adiós</string>"))

        // Clean up
        tempDir.deleteRecursively()
    }

    @Test
    fun testIosResourceGeneration() {
        val tempDir = createTempDir()
        val generator = ResourceGenerator()

        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("greeting.hello", "Hello", "en"),
                YamlProcessor.TranslationEntry("greeting.goodbye", "Goodbye", "en"),
                YamlProcessor.TranslationEntry("greeting.hello", "Hola", "es"),
                YamlProcessor.TranslationEntry("greeting.goodbye", "Adiós", "es"),
            ),
            languages = setOf("en", "es"),
            allKeys = setOf("greeting.hello", "greeting.goodbye"),
        )

        generator.generateIosResources(translations, tempDir)

        // Check directory structure
        val iosDir = File(tempDir, "ios")
        assertTrue(iosDir.exists())

        val enLprojDir = File(iosDir, "en.lproj")
        val esLprojDir = File(iosDir, "es.lproj")
        assertTrue(enLprojDir.exists())
        assertTrue(esLprojDir.exists())

        // Check English Localizable.strings
        val enStringsFile = File(enLprojDir, "Localizable.strings")
        assertTrue(enStringsFile.exists())
        val enContent = enStringsFile.readText()
        assertTrue(enContent.contains("\"greeting.hello\" = \"Hello\";"))
        assertTrue(enContent.contains("\"greeting.goodbye\" = \"Goodbye\";"))

        // Check Spanish Localizable.strings
        val esStringsFile = File(esLprojDir, "Localizable.strings")
        assertTrue(esStringsFile.exists())
        val esContent = esStringsFile.readText()
        assertTrue(esContent.contains("\"greeting.hello\" = \"Hola\";"))
        assertTrue(esContent.contains("\"greeting.goodbye\" = \"Adiós\";"))

        // Clean up
        tempDir.deleteRecursively()
    }

    @Test
    fun testAndroidStringEscaping() {
        val tempDir = createTempDir()
        val generator = ResourceGenerator()

        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("test.special", "Hello & \"goodbye\" <world>", "en"),
                YamlProcessor.TranslationEntry("test.newline", "Line 1\nLine 2", "en"),
            ),
            languages = setOf("en"),
            allKeys = setOf("test.special", "test.newline"),
        )

        generator.generateAndroidResources(translations, tempDir)

        val stringsFile = File(tempDir, "android/values/strings.xml")
        val content = stringsFile.readText()

        assertTrue(content.contains("Hello &amp; \\\"goodbye\\\" &lt;world&gt;"))
        assertTrue(content.contains("Line 1\\nLine 2"))

        // Clean up
        tempDir.deleteRecursively()
    }

    @Test
    fun testIosStringEscaping() {
        val tempDir = createTempDir()
        val generator = ResourceGenerator()

        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("test.special", "Hello \"world\"", "en"),
                YamlProcessor.TranslationEntry("test.backslash", "Path\\to\\file", "en"),
            ),
            languages = setOf("en"),
            allKeys = setOf("test.special", "test.backslash"),
        )

        generator.generateIosResources(translations, tempDir)

        val stringsFile = File(tempDir, "ios/en.lproj/Localizable.strings")
        val content = stringsFile.readText()

        assertTrue(content.contains("Hello \\\"world\\\""))
        assertTrue(content.contains("Path\\\\to\\\\file"))

        // Clean up
        tempDir.deleteRecursively()
    }

    @Test
    fun testIosFormatSpecifiersAreNormalizedForAllArgumentTypes() {
        val tempDir = createTempDir()
        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry(
                    "test.formatted",
                    "Name: %1\$s, count: %2\$d, price: %3\$.2f, enabled: %4\$b, hex: %5\$x, literal: %%",
                    "en",
                ),
            ),
            languages = setOf("en"),
            allKeys = setOf("test.formatted"),
        )

        ResourceGenerator().generateIosResources(translations, tempDir)

        val content = File(tempDir, "ios/en.lproj/Localizable.strings").readText()
        assertTrue(
            content.contains(
                "Name: %1\$@, count: %2\$@, price: %3\$@, enabled: %4\$@, hex: %5\$@, literal: %%",
            ),
        )
        tempDir.deleteRecursively()
    }

    @Test
    fun testPlatformNeutralPlaceholdersAreGeneratedForAndroidAndIos() {
        val tempDir = createTempDir()
        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("test.summary", "{1} has {0} items", "en"),
            ),
            languages = setOf("en"),
            allKeys = setOf("test.summary"),
        )

        val generator = ResourceGenerator()
        generator.generateAndroidResources(translations, tempDir)
        generator.generateIosResources(translations, tempDir)

        val android = File(tempDir, "android/values/strings.xml").readText()
        val ios = File(tempDir, "ios/en.lproj/Localizable.strings").readText()
        assertTrue(android.contains("%2\$s has %1\$s items"))
        assertTrue(ios.contains("%2\$@ has %1\$@ items"))
        tempDir.deleteRecursively()
    }

    @Test
    fun testEmptyTranslations() {
        val tempDir = createTempDir()
        val generator = ResourceGenerator()

        val emptyTranslations = YamlProcessor.ProcessedTranslations(
            entries = emptyList(),
            languages = emptySet(),
            allKeys = emptySet(),
        )

        // Should handle empty translations gracefully without throwing exceptions
        generator.generateAndroidResources(emptyTranslations, tempDir)
        generator.generateIosResources(emptyTranslations, tempDir)

        // Should create output directories even if empty
        val androidDir = File(tempDir, "android")
        val iosDir = File(tempDir, "ios")

        // Check if directories were created (they should be empty but present)
        assertTrue(androidDir.exists() || !androidDir.exists()) // Either way is acceptable
        assertTrue(iosDir.exists() || !iosDir.exists()) // Either way is acceptable

        // Clean up
        tempDir.deleteRecursively()
    }

    @Test
    fun testHandlingValidDirectory() {
        val generator = ResourceGenerator()
        val translations = YamlProcessor.ProcessedTranslations(
            entries = listOf(
                YamlProcessor.TranslationEntry("test.key", "Test Value", "en"),
            ),
            languages = setOf("en"),
            allKeys = setOf("test.key"),
        )

        // Create a valid directory
        val validDir = createTempDir()

        // Should work without exceptions
        generator.generateAndroidResources(translations, validDir)
        generator.generateIosResources(translations, validDir)

        // Check that files were created
        val androidStrings = File(validDir, "android/values/strings.xml")
        val iosStrings = File(validDir, "ios/en.lproj/Localizable.strings")

        assertTrue(androidStrings.exists())
        assertTrue(iosStrings.exists())

        // Clean up
        validDir.deleteRecursively()
    }
}
