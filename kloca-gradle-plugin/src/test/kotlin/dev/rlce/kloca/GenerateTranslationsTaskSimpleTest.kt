package dev.rlce.kloca

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerateTranslationsTaskSimpleTest {

    @Test
    fun testTaskExists() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.rlce.kloca")

        val task = project.tasks.findByName("generateTranslations")
        assertNotNull(task)
        assertTrue(task is GenerateTranslationsTask)
    }

    @Test
    fun testTaskConfiguration() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.rlce.kloca")

        val task = project.tasks.getByName("generateTranslations") as GenerateTranslationsTask
        assertEquals("localization", task.group)
        assertEquals("Generate translations from YAML files", task.description)
    }

    @Test
    fun testTaskType() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.rlce.kloca")

        val task = project.tasks.getByName("generateTranslations")
        assertTrue(task is GenerateTranslationsTask)
    }

    @Test
    fun testAllCompileTasksDependOnTranslationGeneration() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.rlce.kloca")

        val compileTasks = listOf(
            project.tasks.register("compileKotlinJvm"),
            project.tasks.register("compileCommonMainKotlinMetadata"),
            project.tasks.register("compileTestJava"),
        )
        val generateTranslations = project.tasks.getByName("generateTranslations")

        compileTasks.forEach { compileTask ->
            assertTrue(
                generateTranslations in compileTask.get().taskDependencies.getDependencies(compileTask.get()),
                "${compileTask.name} should depend on generateTranslations",
            )
        }
    }

    @Test
    fun testNonCompileTasksDoNotDependOnTranslationGeneration() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("io.github.rlce.kloca")

        val checkTask = project.tasks.register("checkTranslations").get()
        val generateTranslations = project.tasks.getByName("generateTranslations")

        assertFalse(generateTranslations in checkTask.taskDependencies.getDependencies(checkTask))
    }
}
