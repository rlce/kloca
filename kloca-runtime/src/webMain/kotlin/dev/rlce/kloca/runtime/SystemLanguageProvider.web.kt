package dev.rlce.kloca.runtime

actual object SystemLanguageProvider {
    actual fun getCurrentLanguageCode(): String =
        getCurrentLocale().substringBefore('-').substringBefore('_')

    actual fun getCurrentLocale(): String = browserLanguage().ifBlank { "en" }
}
