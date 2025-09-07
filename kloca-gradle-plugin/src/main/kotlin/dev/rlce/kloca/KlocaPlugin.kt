package dev.rlce.kloca

import com.google.devtools.ksp.gradle.KspExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class KlocaPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        // Apply KSP plugin
        project.pluginManager.apply("com.google.devtools.ksp")

        // Create extension for optional configuration
        val extension = project.extensions.create("kloca", KlocaExtension::class.java)

        // Register translation generation task with opinionated defaults
        val generateTranslationsTask = project.tasks.register("generateTranslations", GenerateTranslationsTask::class.java) { task ->
            task.group = "localization"
            task.description = "Generate translations from YAML files"

            // Opinionated defaults with extension override
            task.sourceDirectory.set(project.layout.projectDirectory.dir("src/main/kloca"))
            task.outputDirectory.set(project.layout.buildDirectory.dir("generated/kloca"))
            task.defaultLanguage.set(extension.defaultLanguage.getOrElse("en"))
            task.namespacePrefix.set(extension.namespacePrefix.getOrElse(project.name.replace("-", "")))

            // Configure output directories for copied resources
            task.androidOutputDirectory.set(project.layout.projectDirectory.dir("src/androidMain/res"))
            task.iosOutputDirectory.set(project.layout.projectDirectory.dir("src/iosMain/resources"))
        }

        // Configure KSP with our internal processor
        project.afterEvaluate {
            val kspExtension = project.extensions.findByType(KspExtension::class.java)
            kspExtension?.apply {
                arg("kloca.sourceDir", "src/main/kloca")
                arg("kloca.outputDir", "build/generated/kloca")
                arg("kloca.defaultLanguage", extension.defaultLanguage.getOrElse("en"))
                arg("kloca.namespacePrefix", extension.namespacePrefix.getOrElse(project.name.replace("-", "")))
            }
            // KSP will automatically discover our processor via service loader
            // since it's now embedded in the plugin jar
        }

        // Configure source sets to include generated directory
        project.afterEvaluate {
            // Add generated source directory to Kotlin source sets
            project.pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
                project.logger.info("Configuring KMP source sets for Kloca")
                val kotlinExtension = project.extensions.getByType(KotlinMultiplatformExtension::class.java)
                val generatedDir = project.layout.buildDirectory.dir("generated/kloca").get().asFile

                try {
                    kotlinExtension.sourceSets.getByName("commonMain").kotlin.srcDir(generatedDir)
                    project.logger.warn("✅ Successfully added $generatedDir to commonMain kotlin source directories")
                } catch (e: Exception) {
                    project.logger.warn("❌ Could not add generated directory to commonMain source set: ${e.message}")
                    project.logger.warn("Exception type: ${e.javaClass.simpleName}")
                    e.printStackTrace()
                }
            }

            // Handle regular Kotlin projects
            project.pluginManager.withPlugin("org.jetbrains.kotlin.jvm") {
                try {
                    val kotlinExtension = project.extensions.getByName("kotlin")
                    val sourceSets = kotlinExtension.javaClass.getMethod("sourceSets").invoke(kotlinExtension)
                    val main = sourceSets.javaClass.getMethod("getByName", String::class.java).invoke(sourceSets, "main")
                    val kotlinSourceSet = main.javaClass.getMethod("getKotlin").invoke(main)
                    val generatedDir = project.layout.buildDirectory.dir("generated/kloca").get().asFile
                    kotlinSourceSet.javaClass.getMethod("srcDir", Any::class.java).invoke(kotlinSourceSet, generatedDir)
                    project.logger.info("Added $generatedDir to main kotlin source directories")
                } catch (e: Exception) {
                    project.logger.warn("Could not add generated directory to main source set: ${e.message}")
                }
            }

            // Make Android resource processing depend on translation generation
            project.tasks.matching { task ->
                task.name.contains("mergeDebugResources") ||
                    task.name.contains("mergeReleaseResources") ||
                    task.name.contains("generateDebugResources") ||
                    task.name.contains("generateReleaseResources")
            }.configureEach { task ->
                task.dependsOn(generateTranslationsTask)
            }

            // Make iOS Compose resource tasks depend on translation generation
            project.tasks.matching { task ->
                task.name.contains("syncComposeResourcesForIos") ||
                    task.name.contains("prepareComposeResourcesTaskForIos") ||
                    task.name.contains("assembleIosMainResources")
            }.configureEach { task ->
                task.dependsOn(generateTranslationsTask)
            }
        }

        // Make compileKotlin depend on generateTranslations
        project.tasks.configureEach { task ->
            if (task.name.startsWith("compileKotlin") || task.name.contains("KotlinMetadata")) {
                task.dependsOn(generateTranslationsTask)
            }
        }
    }
}
