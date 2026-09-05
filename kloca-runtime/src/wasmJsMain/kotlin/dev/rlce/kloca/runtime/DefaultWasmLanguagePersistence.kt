@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package dev.rlce.kloca.runtime

import kotlin.JsFun

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun saveToLocalStorage(key: String, value: String)

@JsFun("(key) => globalThis.localStorage.getItem(key)")
private external fun loadFromLocalStorage(key: String): String?

@JsFun("(key) => globalThis.localStorage.removeItem(key)")
private external fun removeFromLocalStorage(key: String)

class DefaultWasmLanguagePersistence : LanguagePersistence {
    override fun saveLanguage(languageCode: String) {
        saveToLocalStorage(PREFERENCE_KEY, languageCode)
    }

    override fun loadLanguage(): String? = loadFromLocalStorage(PREFERENCE_KEY)

    override fun clearLanguage() {
        removeFromLocalStorage(PREFERENCE_KEY)
    }

    private companion object {
        const val PREFERENCE_KEY = "kloca_preferred_language"
    }
}
