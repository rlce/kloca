package dev.rlce.kloca.runtime

actual object UserLanguageStringProvider {
    private var persistence: LanguagePersistence? = null
    private var preferredLanguage: String? = null
    private var availableLanguages: List<String> = emptyList()

    actual fun initialize(
        context: Any,
        availableLanguages: Array<String>,
        languagePersistenceFactory: LanguagePersistenceFactory,
    ) {
        require(context is KlocaTranslationResources) {
            "Wasm initialization requires the generated KlocaTranslationResources object as context"
        }
        WasmKlocaStore.resources = context
        this.availableLanguages = availableLanguages.toList()
        persistence = languagePersistenceFactory.createLanguagePersistence(context)
        preferredLanguage = persistence?.loadLanguage()
    }

    actual fun setLanguage(languageCode: String) {
        preferredLanguage = languageCode.takeIf { it.isNotBlank() }
        preferredLanguage?.let { persistence?.saveLanguage(it) } ?: persistence?.clearLanguage()
    }

    actual fun getCurrentLanguage(): String? = preferredLanguage

    actual fun getString(key: String): String = WasmKlocaStore.lookup(preferredLanguage, key) ?: key

    actual fun getString(key: String, vararg args: Any): String =
        WasmKlocaStore.format(getString(key), args)

    actual fun hasTranslation(key: String): Boolean = WasmKlocaStore.lookup(preferredLanguage, key) != null

    actual fun getAvailableLanguages(): List<String> = availableLanguages
}
