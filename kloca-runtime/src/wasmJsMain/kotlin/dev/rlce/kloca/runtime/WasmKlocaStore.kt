package dev.rlce.kloca.runtime

internal object WasmKlocaStore {
    lateinit var resources: KlocaTranslationResources

    fun lookup(language: String?, key: String): String? {
        if (!::resources.isInitialized) return null
        val requestedLanguage = language ?: SystemLanguageProvider.getCurrentLanguageCode()
        val normalizedLanguage = requestedLanguage.replace('_', '-')
        val languageOnly = normalizedLanguage.substringBefore('-')
        return resources.translations[normalizedLanguage]?.get(key)
            ?: resources.translations[languageOnly]?.get(key)
            ?: resources.translations[resources.defaultLanguage]?.get(key)
    }

    fun format(value: String, args: Array<out Any>): String {
        var result = value
        args.forEachIndexed { index, argument ->
            result = result.replace("%${index + 1}\$s", argument.toString())
        }
        args.forEach { argument -> result = result.replaceFirst("%s", argument.toString()) }
        return result
    }
}
