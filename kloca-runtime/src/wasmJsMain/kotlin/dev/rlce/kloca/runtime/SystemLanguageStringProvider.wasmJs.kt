package dev.rlce.kloca.runtime

actual object SystemLanguageStringProvider {
    actual fun initialize(context: Any) {
        require(context is KlocaTranslationResources) {
            "Wasm initialization requires the generated KlocaTranslationResources object as context"
        }
        WasmKlocaStore.resources = context
    }

    actual fun get(key: String): String = WasmKlocaStore.lookup(null, key) ?: key

    actual fun get(key: String, vararg args: Any): String =
        WasmKlocaStore.format(get(key), args)

    actual fun getCurrentLanguage(): String = SystemLanguageProvider.getCurrentLanguageCode()

    actual fun clearCache() = Unit
}
