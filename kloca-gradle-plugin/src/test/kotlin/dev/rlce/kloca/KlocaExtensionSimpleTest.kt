package dev.rlce.kloca

import org.gradle.testfixtures.ProjectBuilder
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class KlocaExtensionSimpleTest {

    @Test
    fun testExtensionCreation() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.rlce.kloca")

        val extension = project.extensions.findByType(KlocaExtension::class.java)
        assertNotNull(extension)
    }

    @Test
    fun testDefaultValues() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.rlce.kloca")

        val extension = project.extensions.findByType(KlocaExtension::class.java)!!

        assertEquals("en", extension.defaultLanguage.getOrElse("fallback"))
        assertEquals("", extension.namespacePrefix.getOrElse("fallback"))
    }

    @Test
    fun testSetValues() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply("dev.rlce.kloca")

        val extension = project.extensions.findByType(KlocaExtension::class.java)!!

        extension.defaultLanguage.set("es")
        extension.namespacePrefix.set("MyApp")

        assertEquals("es", extension.defaultLanguage.get())
        assertEquals("MyApp", extension.namespacePrefix.get())
    }
}
