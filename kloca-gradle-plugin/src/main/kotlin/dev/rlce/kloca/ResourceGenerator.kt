package dev.rlce.kloca

import java.io.File

/**
 * Generates platform-specific localization resources from processed YAML translations.
 *
 * This class handles the conversion of YAML-based translation data into:
 * - Android XML string resources (values/strings.xml format)
 * - iOS localization bundles (.lproj/Localizable.strings format)
 * - Wasm JSON resources
 *
 * The generated resources follow platform conventions and handle proper escaping
 * for each platform's resource format requirements.
 */
class ResourceGenerator {

    fun generateWasmResources(
        translations: YamlProcessor.ProcessedTranslations,
        outputDir: File,
        defaultLanguage: String = "en",
    ) {
        val wasmDir = File(outputDir, "wasmJs/kloca")
        wasmDir.mkdirs()
        val languages = translations.languages.sorted().joinToString(",\n") { language ->
            val entries = translations.entries
                .filter { it.language == language }
                .sortedBy { it.key }
                .joinToString(",\n") { entry ->
                    "      \"${escapeJson(entry.key)}\": \"${escapeJson(entry.value)}\""
                }
            "    \"${escapeJson(language)}\": {\n$entries\n    }"
        }
        File(wasmDir, "translations.json").writeText(
            "{\n  \"defaultLanguage\": \"${escapeJson(defaultLanguage)}\",\n  \"translations\": {\n$languages\n  }\n}\n",
        )
    }

    private fun escapeJson(value: String): String = buildString {
        value.forEach { char ->
            when (char) {
                '\\' -> append("\\\\")
                '"' -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> append(char)
            }
        }
    }

    /**
     * Generates Android-compatible XML string resources from translations.
     *
     * Creates the standard Android resource structure:
     * - values/strings.xml for default language
     * - values-{language}/strings.xml for other languages
     *
     * @param translations Processed translation data from YAML files
     * @param outputDir Base output directory for generated resources
     * @param defaultLanguage Language code to use for the default values/ directory
     */
    fun generateAndroidResources(translations: YamlProcessor.ProcessedTranslations, outputDir: File, defaultLanguage: String = "en") {
        // Generate resources directly in the format expected by Android builds
        val androidResDir = File(outputDir, "android")

        translations.languages.forEach { language ->
            val languageEntries = translations.entries.filter { it.language == language }

            val valuesDir = if (language == defaultLanguage) {
                File(androidResDir, "values")
            } else {
                File(androidResDir, "values-$language")
            }
            valuesDir.mkdirs()

            val stringsXml = generateAndroidStringsXml(languageEntries)
            val stringsFile = File(valuesDir, "strings.xml")
            stringsFile.writeText(stringsXml)
        }
    }

    /**
     * Generates the XML content for Android string resources.
     *
     * @param entries Translation entries for a specific language
     * @return Formatted XML string ready to write to strings.xml
     */
    private fun generateAndroidStringsXml(entries: List<YamlProcessor.TranslationEntry>): String {
        val stringEntries = entries.sortedBy { it.key }.joinToString("\n") { entry ->
            val escapedValue = escapeAndroidString(normalizeAndroidFormatSpecifiers(entry.value))
            "    <string name=\"${entry.key.replace(".", "_")}\">$escapedValue</string>"
        }

        return """<?xml version="1.0" encoding="utf-8"?>
<resources>
$stringEntries
</resources>"""
    }

    /**
     * Escapes special characters for Android XML string resources.
     *
     * Handles Android-specific escaping requirements including:
     * - XML entities (&, <, >)
     * - Quote escaping for strings
     * - Newline and tab characters
     *
     * @param value Raw string value to escape
     * @return Properly escaped string safe for Android XML
     */
    private fun escapeAndroidString(value: String): String {
        return value
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "\\\"")
            .replace("'", "\\'")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
    }

    private fun normalizeAndroidFormatSpecifiers(value: String): String =
        KLOCA_PLACEHOLDER.replace(value) { match ->
            "%${match.groupValues[1].toInt() + 1}\$s"
        }

    /**
     * Generates iOS-compatible localization bundles from translations.
     *
     * Creates the standard iOS localization structure:
     * - {language}.lproj/Localizable.strings for each language
     *
     * The generated .lproj bundles follow iOS localization conventions
     * and are automatically discovered by NSBundle at runtime.
     *
     * @param translations Processed translation data from YAML files
     * @param outputDir Base output directory for generated resources
     * @param defaultLanguage Language code (currently unused for iOS, all languages treated equally)
     */
    fun generateIosResources(translations: YamlProcessor.ProcessedTranslations, outputDir: File, defaultLanguage: String = "en") {
        val iosDir = File(outputDir, "ios")
        iosDir.mkdirs()

        // Generate .lproj structure for iOS (standard iOS localization)
        translations.languages.forEach { language ->
            val languageEntries = translations.entries.filter { it.language == language }

            val lprojDir = File(iosDir, "$language.lproj")
            lprojDir.mkdirs()

            val stringsContent = generateIosStringsFile(languageEntries)
            val stringsFile = File(lprojDir, "Localizable.strings")
            stringsFile.writeText(stringsContent)
        }
    }

    /**
     * Generates the content for iOS Localizable.strings files.
     *
     * @param entries Translation entries for a specific language
     * @return Formatted strings content ready to write to Localizable.strings
     */
    private fun generateIosStringsFile(entries: List<YamlProcessor.TranslationEntry>): String {
        return entries.sortedBy { it.key }.joinToString("\n") { entry ->
            val escapedValue = escapeIosString(entry.value)
            "\"${entry.key}\" = \"$escapedValue\";"
        }
    }

    /**
     * Escapes special characters for iOS strings files.
     *
     * Handles iOS-specific escaping requirements including:
     * - Backslash escaping
     * - Quote escaping for strings
     * - Newline, tab, and carriage return characters
     *
     * @param value Raw string value to escape
     * @return Properly escaped string safe for iOS Localizable.strings
     */
    private fun escapeIosString(value: String): String {
        return normalizeIosFormatSpecifiers(value)
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\t", "\\t")
            .replace("\r", "\\r")
    }

    /**
     * Kloca translation sources use Android/Java printf placeholders. Apple string
     * resources use %@ for object values, so convert the supported value
     * placeholders while preserving their optional positional index.
     *
     * Formatting is performed by the Kloca runtime rather than NSString, which
     * means representing every Kotlin argument as an object is both safe and
     * avoids ABI-specific numeric specifiers such as %ld and %lld.
     */
    private fun normalizeIosFormatSpecifiers(value: String): String =
        JAVA_VALUE_PLACEHOLDER.replace(
            KLOCA_PLACEHOLDER.replace(value) { match ->
                "%${match.groupValues[1].toInt() + 1}\$@"
            },
        ) { match ->
            "%${match.groupValues[1]}@"
        }

    private companion object {
        // Java Formatter value conversions, including flags, width and precision.
        // %% and %n are intentionally not value placeholders.
        val JAVA_VALUE_PLACEHOLDER =
            Regex("%(\\d+\\$)?[-#+ 0,(<]*\\d*(?:\\.\\d+)?(?:[bBhHsScCdoxXeEfgGaA]|[tT][A-Za-z])")
        val KLOCA_PLACEHOLDER = Regex("\\{(\\d+)}")
    }
}
