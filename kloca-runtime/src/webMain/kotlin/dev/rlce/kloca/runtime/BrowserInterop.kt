package dev.rlce.kloca.runtime

/** Browser calls whose interop syntax differs between the Wasm and JS targets. */
internal expect fun browserLanguage(): String

internal expect fun localStorageSetItem(key: String, value: String)

internal expect fun localStorageGetItem(key: String): String?

internal expect fun localStorageRemoveItem(key: String)
