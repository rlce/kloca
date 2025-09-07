package dev.rlce.kloca

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GenerateTranslationsTaskSimpleTest {

    @Test
    fun testTaskExists() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.rlce.kloca")

        val task = project.tasks.findByName("generateTranslations")
        assertNotNull(task)
        assertTrue(task is GenerateTranslationsTask)
    }

    @Test
    fun testTaskConfiguration() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.rlce.kloca")

        val task = project.tasks.getByName("generateTranslations") as GenerateTranslationsTask
        assertEquals("localization", task.group)
        assertEquals("Generate translations from YAML files", task.description)
    }

    @Test
    fun testTaskType() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.rlce.kloca")

        val task = project.tasks.getByName("generateTranslations")
        assertTrue(task is GenerateTranslationsTask)
    }
}
