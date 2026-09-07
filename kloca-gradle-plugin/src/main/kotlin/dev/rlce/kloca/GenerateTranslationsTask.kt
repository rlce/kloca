package dev.rlce.kloca

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GenerateTranslationsTask : DefaultTask() {

    @get:InputDirectory
    abstract val sourceDirectory: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @get:Input
    abstract val defaultLanguage: Property<String>

    @get:Input
    abstract val namespacePrefix: Property<String>

    @get:Internal
    abstract val androidOutputDirectory: DirectoryProperty

    @get:Internal
    abstract val iosOutputDirectory: DirectoryProperty

    @get:Internal
    abstract val wasmJsOutputDirectory: DirectoryProperty

    init {
        // Force task to run if expected output files don't exist
        outputs.upToDateWhen {
            val outputDir = outputDirectory.asFile.get()
            val iosMainResDir = iosOutputDirectory.asFile.get()
            val wasmJsMainResDir = wasmJsOutputDirectory.asFile.get()

            // Check if core generated files exist
            val stringKeysExists = File(outputDir, "StringKeys.kt").exists()

            // Check if iOS resources exist for all expected languages
            val sourceDir = sourceDirectory.asFile.get()
            if (!sourceDir.exists()) return@upToDateWhen stringKeysExists

            val processor = YamlProcessor()
            val expectedLanguages = try {
                processor.processYamlFiles(sourceDir).languages
            } catch (e: Exception) {
                return@upToDateWhen false
            }

            val allIosResourcesExist = expectedLanguages.all { language ->
                File(iosMainResDir, "$language.lproj/Localizable.strings").exists()
            }

            val wasmResourcesExist = File(wasmJsMainResDir, "kloca/translations.json").exists()

            stringKeysExists && allIosResourcesExist && wasmResourcesExist
        }
    }

    @TaskAction
    fun generateTranslations() {
        val sourceDir = sourceDirectory.asFile.get()
        val outputDir = outputDirectory.asFile.get()

        if (!sourceDir.exists()) {
            logger.warn("Source directory does not exist: ${sourceDir.absolutePath}")
            return
        }

        val processor = YamlProcessor()
        val resourceGenerator = ResourceGenerator()

        try {
            // Process YAML files
            val translations = processor.processYamlFiles(sourceDir)

            // Validate translations
            val errors = processor.validateTranslations(translations)
            if (errors.isNotEmpty()) {
                errors.forEach { logger.warn(it) }
            }

            // Create output directory
            outputDir.mkdirs()
            listOf("android", "ios", "wasmJs").forEach { platform ->
                File(outputDir, platform).deleteRecursively()
            }

            // Generate StringKeys.kt (always enabled)
            val stringKeysContent = processor.generateStringKeysClass(
                translations.allKeys,
                namespacePrefix.get(),
            )
            val stringKeysFile = File(outputDir, "StringKeys.kt")
            stringKeysFile.writeText(stringKeysContent)
            logger.info("Generated StringKeys.kt")

            File(outputDir, "Translations.kt").writeText(
                processor.generateTranslationResourcesClass(
                    translations,
                    namespacePrefix.get(),
                    defaultLanguage.get(),
                ),
            )
            logger.info("Generated Translations.kt")

            // Generate platform resources
            resourceGenerator.generateAndroidResources(translations, outputDir, defaultLanguage.get())
            resourceGenerator.generateIosResources(translations, outputDir, defaultLanguage.get())
            resourceGenerator.generateWasmResources(translations, outputDir, defaultLanguage.get())

            // Copy Android resources to main source set
            copyAndroidResourcesToMainSourceSet(outputDir, androidOutputDirectory.asFile.get())

            // Copy iOS resources to main source set
            copyIosResourcesToMainSourceSet(outputDir, iosOutputDirectory.asFile.get())

            copyWasmResourcesToMainSourceSet(outputDir, wasmJsOutputDirectory.asFile.get())

            logger.info("Translation generation completed successfully")
        } catch (e: Exception) {
            logger.error("Failed to generate translations", e)
            throw e
        }
    }

    private fun copyWasmResourcesToMainSourceSet(outputDir: File, targetResourceDir: File) {
        val wasmResourceDir = File(outputDir, "wasmJs")
        if (!wasmResourceDir.exists()) return
        wasmResourceDir.copyRecursively(targetResourceDir, overwrite = true)
    }

    private fun copyAndroidResourcesToMainSourceSet(outputDir: File, targetResDir: File) {
        val androidResourceDir = File(outputDir, "android")
        if (!androidResourceDir.exists()) {
            return
        }

        // Create target directory if it doesn't exist
        targetResDir.mkdirs()

        // Copy all values directories
        androidResourceDir.listFiles()?.forEach { valuesDir ->
            if (valuesDir.isDirectory && valuesDir.name.startsWith("values")) {
                val targetValuesDir = File(targetResDir, valuesDir.name)
                targetValuesDir.mkdirs()

                valuesDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        val targetFile = File(targetValuesDir, file.name)
                        file.copyTo(targetFile, overwrite = true)
                        logger.info("Copied ${file.absolutePath} to ${targetFile.absolutePath}")
                    }
                }
            }
        }
    }

    private fun copyIosResourcesToMainSourceSet(outputDir: File, iosMainResDir: File) {
        val iosResourceDir = File(outputDir, "ios")
        if (!iosResourceDir.exists()) {
            return
        }

        // Create target directory if it doesn't exist
        iosMainResDir.mkdirs()

        // Copy .lproj directories to iosMain/resources (standard iOS localization)
        iosResourceDir.listFiles()?.forEach { lprojDir ->
            if (lprojDir.isDirectory && lprojDir.name.endsWith(".lproj")) {
                val targetLprojDir = File(iosMainResDir, lprojDir.name)
                targetLprojDir.mkdirs()

                lprojDir.listFiles()?.forEach { file ->
                    if (file.isFile) {
                        val targetFile = File(targetLprojDir, file.name)
                        file.copyTo(targetFile, overwrite = true)
                        logger.info("Copied ${file.absolutePath} to ${targetFile.absolutePath}")
                    }
                }
            }
        }
    }
}
