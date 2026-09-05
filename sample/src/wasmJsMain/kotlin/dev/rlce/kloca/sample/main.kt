package dev.rlce.kloca.sample

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import dev.rlce.kloca.generated.SampleTranslations
import dev.rlce.kloca.runtime.DefaultWasmLanguagePersistence
import dev.rlce.kloca.runtime.startKloca

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    startKloca(
        context = SampleTranslations,
        availableLanguages = SampleTranslations.translations.keys.toTypedArray(),
        languagePersistenceFactory = { DefaultWasmLanguagePersistence() },
    )
    ComposeViewport("composeApp") { SampleApp() }
}
