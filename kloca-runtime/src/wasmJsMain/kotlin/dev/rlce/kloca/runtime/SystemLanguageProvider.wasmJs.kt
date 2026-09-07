@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package dev.rlce.kloca.runtime

import kotlin.JsFun

@JsFun("() => globalThis.navigator?.language ?? 'en'")
private external fun browserLanguage(): String

actual object SystemLanguageProvider {
    actual fun getCurrentLanguageCode(): String =
        getCurrentLocale().substringBefore('-').substringBefore('_')

    actual fun getCurrentLocale(): String = browserLanguage().ifBlank { "en" }
}
