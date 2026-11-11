@file:OptIn(kotlin.js.ExperimentalWasmJsInterop::class)

package dev.rlce.kloca.runtime

import kotlin.JsFun

@JsFun("() => globalThis.navigator?.language ?? 'en'")
private external fun jsBrowserLanguage(): String

@JsFun("(key, value) => globalThis.localStorage.setItem(key, value)")
private external fun jsSetItem(key: String, value: String)

@JsFun("(key) => globalThis.localStorage.getItem(key)")
private external fun jsGetItem(key: String): String?

@JsFun("(key) => globalThis.localStorage.removeItem(key)")
private external fun jsRemoveItem(key: String)

internal actual fun browserLanguage(): String = jsBrowserLanguage()

internal actual fun localStorageSetItem(key: String, value: String) = jsSetItem(key, value)

internal actual fun localStorageGetItem(key: String): String? = jsGetItem(key)

internal actual fun localStorageRemoveItem(key: String) = jsRemoveItem(key)
