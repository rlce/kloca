package dev.rlce.kloca

import org.yaml.snakeyaml.Yaml
import java.io.File
import java.io.FileInputStream

/**
 * Processes YAML translation files and transforms them into structured data.
 *
 * This class handles:
 * - YAML file parsing with validation
 * - Nested key flattening (e.g., "feature.screen.title" from nested YAML)
 * - Language extraction from filenames
 * - Translation validation across languages
 * - StringKeys.kt code generation
 *
 * Supported file naming conventions:
 * - i18n.en.yaml → English
 * - i18n.es-ES.yaml → Spanish (Spain)
 * - en.yaml → English (fallback)
 * - i18n.yaml → Default language
 */
class YamlProcessor {
    /**
     * Represents a single translation entry.
     *
     * @property key Dot-notation key (e.g., "greeting.hello")
     * @property value Translated string value
     * @property language Language code (e.g., "en", "es")
     */
    data class TranslationEntry(
        val key: String,
        val value: String,
        val language: String,
    )

    /**
     * Container for processed translation data.
     *
     * @property entries All translation entries from all files
     * @property languages Set of all discovered language codes
     * @property allKeys Set of all unique translation keys
     */
    data class ProcessedTranslations(
        val entries: List<TranslationEntry>,
        val languages: Set<String>,
        val allKeys: Set<String>,
    )

    /**
     * Processes all YAML files in the source directory.
     *
     * Discovers and processes all .yaml/.yml files, extracting language
     * codes from filenames and flattening nested structures into
     * dot-notation keys.
     *
     * @param sourceDir Directory containing YAML translation files
     * @return ProcessedTranslations containing all parsed data
     * @throws IllegalArgumentException if source directory doesn't exist or contains invalid data
     * @throws RuntimeException if YAML parsing fails
     */
    fun processYamlFiles(sourceDir: File): ProcessedTranslations {
        if (!sourceDir.exists()) {
            throw IllegalArgumentException("Source directory does not exist: ${sourceDir.absolutePath}")
        }

        if (!sourceDir.isDirectory) {
            throw IllegalArgumentException("Source path is not a directory: ${sourceDir.absolutePath}")
        }

        val yaml = Yaml()
        val entries = mutableListOf<TranslationEntry>()
        val languages = mutableSetOf<String>()
        val allKeys = mutableSetOf<String>()

        val yamlFiles = sourceDir.listFiles { _, name ->
            name.endsWith(".yaml") || name.endsWith(".yml")
        }

        if (yamlFiles == null || yamlFiles.isEmpty()) {
            throw IllegalStateException("No YAML files found in directory: ${sourceDir.absolutePath}")
        }

        yamlFiles.forEach { file ->
            try {
                val language = extractLanguageFromFilename(file.name)
                if (language.isBlank()) {
                    throw IllegalArgumentException("Could not extract language from filename: ${file.name}")
                }

                // Validate language code format (basic check)
                if (!language.matches(Regex("^[a-z]{2}(-[A-Z]{2})?$"))) {
                    println("Warning: Language code '$language' from file '${file.name}' may not be valid")
                }

                languages.add(language)

                FileInputStream(file).use { input ->
                    val data = try {
                        yaml.load<Map<String, Any>>(input)
                    } catch (e: Exception) {
                        throw IllegalArgumentException("Failed to parse YAML file ${file.name}: ${e.message}", e)
                    }

                    if (data == null) {
                        throw IllegalArgumentException("YAML file ${file.name} is empty or contains no data")
                    }

                    // Expect 'i18n' as root key, containing all translations
                    val i18nData = data["i18n"] as? Map<String, Any> ?: data
                    val flattenedEntries = flattenYamlMap(i18nData, language, file.name)

                    if (flattenedEntries.isEmpty()) {
                        println("Warning: No translation entries found in file: ${file.name}")
                    }

                    entries.addAll(flattenedEntries)
                    allKeys.addAll(flattenedEntries.map { it.key })
                }
            } catch (e: Exception) {
                throw RuntimeException("Error processing YAML file ${file.name}: ${e.message}", e)
            }
        }

        if (entries.isEmpty()) {
            throw IllegalStateException("No translation entries found in any YAML files")
        }

        return ProcessedTranslations(entries, languages, allKeys)
    }

    private fun extractLanguageFromFilename(filename: String): String {
        // Support patterns: i18n.en.yaml, i18n.es-ES.yaml, en.yaml, etc.
        val nameWithoutExtension = filename.substringBeforeLast(".")

        return when {
            nameWithoutExtension.startsWith("i18n.") -> {
                nameWithoutExtension.substringAfter("i18n.")
            }
            nameWithoutExtension == "i18n" -> "en" // Default language
            else -> nameWithoutExtension // Fallback to filename
        }
    }

    private fun flattenYamlMap(
        data: Map<String, Any>,
        language: String,
        fileName: String,
        prefix: String = "",
    ): List<TranslationEntry> {
        val entries = mutableListOf<TranslationEntry>()

        data.forEach { (key, value) ->
            if (key.isBlank()) {
                throw IllegalArgumentException("Empty key found in YAML file $fileName")
            }

            // Validate key format (no invalid characters)
            if (!key.matches(Regex("^[a-zA-Z0-9_.-]+$"))) {
                throw IllegalArgumentException(
                    "Invalid key '$key' in file $fileName. Keys can only contain letters, numbers, dots, hyphens, and underscores.",
                )
            }

            val fullKey = if (prefix.isEmpty()) key else "$prefix.$key"

            when (value) {
                is Map<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    val nestedMap = value as? Map<String, Any>
                        ?: throw IllegalArgumentException("Invalid nested structure in key '$fullKey' in file $fileName")

                    entries.addAll(
                        flattenYamlMap(nestedMap, language, fileName, fullKey),
                    )
                }
                is String -> {
                    if (value.isNotEmpty()) {
                        entries.add(TranslationEntry(fullKey, value, language))
                    } else {
                        println("Warning: Empty string value for key '$fullKey' in file $fileName")
                    }
                }
                else -> {
                    val stringValue = value.toString()
                    if (stringValue.isNotEmpty()) {
                        entries.add(TranslationEntry(fullKey, stringValue, language))
                    } else {
                        println("Warning: Empty value for key '$fullKey' in file $fileName")
                    }
                }
            }
        }

        return entries
    }

    /**
     * Generates Kotlin source code for the StringKeys object.
     *
     * Creates a type-safe object with string constants for all translation keys.
     * Keys are converted from dot notation to SCREAMING_SNAKE_CASE constants.
     *
     * @param allKeys Set of all translation keys in dot notation
     * @param namespacePrefix Optional prefix for the generated class name
     * @return Complete Kotlin source code ready to write to StringKeys.kt
     */
    fun generateStringKeysClass(allKeys: Set<String>, namespacePrefix: String): String {
        val className = if (namespacePrefix.isNotEmpty()) {
            "${namespacePrefix.replaceFirstChar { it.uppercase() }}StringKeys"
        } else {
            "StringKeys"
        }

        val constants = allKeys.sorted().map { key ->
            val constantName = keyToConstantName(key)
            "    const val $constantName = \"$key\""
        }.joinToString("\n")

        return """
package dev.rlce.kloca.generated

/**
 * Generated string keys for localization.
 * Do not modify this file manually.
 */
object $className {
$constants
}
        """.trimIndent()
    }

    /** Generates an in-binary translation table used by the Wasm runtime. */
    fun generateTranslationResourcesClass(
        translations: ProcessedTranslations,
        namespacePrefix: String,
        defaultLanguage: String,
    ): String {
        val className = if (namespacePrefix.isNotEmpty()) {
            "${namespacePrefix.replaceFirstChar { it.uppercase() }}Translations"
        } else {
            "Translations"
        }
        val languages = translations.languages.sorted().joinToString(",\n") { language ->
            val entries = translations.entries
                .filter { it.language == language }
                .sortedBy { it.key }
                .joinToString(",\n") { entry ->
                    "            \"${escapeKotlin(entry.key)}\" to \"${escapeKotlin(entry.value)}\""
                }
            "        \"${escapeKotlin(language)}\" to mapOf(\n$entries\n        )"
        }

        return """
package dev.rlce.kloca.generated

import dev.rlce.kloca.runtime.KlocaTranslationResources

/** Generated translations for web/Wasm. Do not modify this file manually. */
object $className : KlocaTranslationResources {
    override val defaultLanguage: String = "${escapeKotlin(defaultLanguage)}"
    override val translations: Map<String, Map<String, String>> = mapOf(
$languages
    )
}
        """.trimIndent()
    }

    private fun escapeKotlin(value: String): String = value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("$", "\\$")
        .replace("\n", "\\n")
        .replace("\r", "\\r")
        .replace("\t", "\\t")

    private fun keyToConstantName(key: String): String {
        return key
            .split(".")
            .joinToString("_") { part ->
                part.replace(Regex("[^a-zA-Z0-9_]"), "_")
                    .uppercase()
            }
    }

    /**
     * Validates translation consistency across all languages.
     *
     * Checks for missing translation keys in any language compared
     * to the complete set of keys. Returns a list of validation
     * warnings/errors for missing translations.
     *
     * @param translations Processed translation data to validate
     * @return List of validation error messages (empty if no issues)
     */
    fun validateTranslations(translations: ProcessedTranslations): List<String> {
        val errors = mutableListOf<String>()
        val keysByLanguage = translations.entries.groupBy { it.language }

        // Check for missing translations
        translations.languages.forEach { language ->
            val keysForLanguage = keysByLanguage[language]?.map { it.key }?.toSet() ?: emptySet()
            val missingKeys = translations.allKeys - keysForLanguage

            if (missingKeys.isNotEmpty()) {
                errors.add("Language '$language' is missing keys: ${missingKeys.joinToString(", ")}")
            }
        }

        return errors
    }
}
