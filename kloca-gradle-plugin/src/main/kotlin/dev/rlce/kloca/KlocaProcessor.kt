package dev.rlce.kloca

import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import java.io.File

/**
 * KSP (Kotlin Symbol Processing) processor for Kloca localization.
 *
 * This processor orchestrates the entire translation generation pipeline:
 * 1. Processes YAML translation files from source directory
 * 2. Validates translation consistency across languages
 * 3. Generates type-safe StringKeys.kt constants
 * 4. Creates platform-specific resource files (Android XML, iOS .lproj)
 *
 * The processor runs during the Kotlin compilation phase and integrates
 * with the KSP annotation processing pipeline, ensuring generated code
 * is available during compilation.
 */
class KlocaProcessor(
    private val environment: SymbolProcessorEnvironment,
) : SymbolProcessor {

    private val yamlProcessor = YamlProcessor()
    private val resourceGenerator = ResourceGenerator()
    private var hasProcessed = false

    /**
     * Main processing method called by KSP framework.
     *
     * This method runs once per compilation and handles the complete
     * translation generation workflow. It uses environment options
     * passed from the Gradle plugin to configure processing behavior.
     *
     * @param resolver KSP resolver (unused - we process YAML files directly)
     * @return Empty list (we don't process Kotlin symbols, only YAML files)
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (hasProcessed) {
            return emptyList()
        }

        try {
            // Extract configuration from environment options (set by Gradle plugin)
            val sourceDir = environment.options["kloca.sourceDir"]?.let { File(it) }
                ?: File("src/main/kloca")
            val outputDir = environment.options["kloca.outputDir"]?.let { File(it) }
                ?: File("build/generated/kloca")
            val defaultLanguage = environment.options["kloca.defaultLanguage"] ?: "en"
            val namespacePrefix = environment.options["kloca.namespacePrefix"] ?: ""

            environment.logger.info("Kloca KSP processor starting with sourceDir: ${sourceDir.absolutePath}")

            if (!sourceDir.exists()) {
                environment.logger.warn("Kloca source directory does not exist: ${sourceDir.absolutePath}")
                hasProcessed = true
                return emptyList()
            }

            // Process YAML files
            val translations = yamlProcessor.processYamlFiles(sourceDir)
            environment.logger.info("Processed ${translations.entries.size} translation entries for languages: ${translations.languages}")

            // Validate translations
            val errors = yamlProcessor.validateTranslations(translations)
            if (errors.isNotEmpty()) {
                errors.forEach { environment.logger.warn("Translation validation: $it") }
            }

            // Create output directory
            outputDir.mkdirs()

            // Generate StringKeys.kt
            val stringKeysContent = yamlProcessor.generateStringKeysClass(
                translations.allKeys,
                namespacePrefix,
            )

            val stringKeysFile = environment.codeGenerator.createNewFile(
                Dependencies(false), // No source dependencies since we generate from YAML
                "dev.rlce.kloca.generated",
                "StringKeys",
            )

            stringKeysFile.write(stringKeysContent.toByteArray())
            environment.logger.info("Generated StringKeys.kt with ${translations.allKeys.size} keys")

            // Generate platform resources
            resourceGenerator.generateAndroidResources(translations, outputDir, defaultLanguage)
            resourceGenerator.generateIosResources(translations, outputDir, defaultLanguage)
            environment.logger.info("Generated platform resources for Android and iOS")

            hasProcessed = true
        } catch (e: Exception) {
            environment.logger.error("Kloca KSP processor failed: ${e.message}")
            throw e
        }

        return emptyList()
    }
}
