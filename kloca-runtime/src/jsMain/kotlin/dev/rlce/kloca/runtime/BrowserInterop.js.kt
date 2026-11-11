package dev.rlce.kloca.runtime

internal actual fun browserLanguage(): String =
    js("globalThis.navigator && globalThis.navigator.language || 'en'") as String

internal actual fun localStorageSetItem(key: String, value: String) {
    js("globalThis.localStorage.setItem(key, value)")
}

internal actual fun localStorageGetItem(key: String): String? =
    js("globalThis.localStorage.getItem(key)") as String?

internal actual fun localStorageRemoveItem(key: String) {
    js("globalThis.localStorage.removeItem(key)")
}
