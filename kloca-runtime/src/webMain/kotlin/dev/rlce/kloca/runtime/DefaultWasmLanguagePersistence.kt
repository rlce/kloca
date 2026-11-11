package dev.rlce.kloca.runtime

/** Stores the preferred language in `localStorage`; used by both the Wasm and JS targets. */
class DefaultWasmLanguagePersistence : LanguagePersistence {
    override fun saveLanguage(languageCode: String) {
        localStorageSetItem(PREFERENCE_KEY, languageCode)
    }

    override fun loadLanguage(): String? = localStorageGetItem(PREFERENCE_KEY)

    override fun clearLanguage() {
        localStorageRemoveItem(PREFERENCE_KEY)
    }

    private companion object {
        const val PREFERENCE_KEY = "kloca_preferred_language"
    }
}
