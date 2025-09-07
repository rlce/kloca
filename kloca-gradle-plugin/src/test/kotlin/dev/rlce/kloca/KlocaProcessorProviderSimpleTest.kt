package dev.rlce.kloca

import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KlocaProcessorProviderSimpleTest {

    @Test
    fun testProviderExists() {
        val provider = KlocaProcessorProvider()
        assertNotNull(provider)
    }

    @Test
    fun testProviderImplementsInterface() {
        val provider = KlocaProcessorProvider()
        assertTrue(provider is com.google.devtools.ksp.processing.SymbolProcessorProvider)
    }
}
