# Kloca API Documentation

Complete API reference for Kloca Kotlin Multiplatform localization library.

## Overview

Kloca provides three main API surfaces:

1. **Kloca Object** - Unified, recommended API
2. **Gradle Plugin DSL** - Configuration and build integration
3. **Compose Integration** - Reactive UI functions

## Core API: Kloca Object

The main entry point for all localization operations.

### Initialization

```kotlin
object Kloca {
    /**
     * Initialize with system language only.
     * @param context Platform-specific context (Application on Android, Unit on iOS)
     * @param languagePersistenceFactory Factory to create LanguagePersistence instance for user language storage
     */
    fun initialize(context: Any, languagePersistenceFactory: LanguagePersistenceFactory)

    /**
     * Initialize with user language selection support.
     * @param context Platform-specific context
     * @param availableLanguages Array of supported language codes
     * @param languagePersistenceFactory Factory to create LanguagePersistence instance for user language storage
     */
    fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory)
}
```

**Usage Examples:**
```kotlin
// Android - System language only
Kloca.initialize(
    context = applicationContext,
    languagePersistenceFactory = { DefaultAndroidLanguagePersistence(applicationContext) }
)

// Android - With user language selection
Kloca.initialize(
    context = applicationContext,
    availableLanguages = arrayOf("en", "es", "fr", "de"),
    languagePersistenceFactory = { DefaultAndroidLanguagePersistence(applicationContext) }
)

// iOS - System language only
Kloca.initialize(
    context = Unit,
    languagePersistenceFactory = { DefaultIosLanguagePersistence() }
)

// iOS - With user language selection
Kloca.initialize(
    context = Unit,
    availableLanguages = arrayOf("en", "es", "fr", "de"),
    languagePersistenceFactory = { DefaultIosLanguagePersistence() }
)
```

### String Access

```kotlin
/**
 * Get localized string by key.
 * @param key Translation key in dot notation
 * @return Localized string or key if not found
 */
fun getString(key: String): String

/**
 * Get formatted localized string with parameters.
 * @param key Translation key
 * @param args Format arguments
 * @return Formatted localized string or key if not found
 */
fun getString(key: String, vararg args: Any): String
```

**Usage Examples:**
```kotlin
val greeting = Kloca.getString("greeting.hello")
val welcome = Kloca.getString("greeting.welcome", "John")

// Type-safe usage (recommended)
val greeting = Kloca.getString(StringKeys.GREETING_HELLO)
val welcome = Kloca.getString(StringKeys.GREETING_WELCOME, "John")
```

### Language Management

```kotlin
/**
 * Set user preferred language and switch to USER_PREFERENCE mode.
 * @param languageCode Language code (e.g., "en", "es", "fr")
 */
fun setUserLanguage(languageCode: String)

/**
 * Switch back to system language mode.
 */
fun useSystemLanguage()

/**
 * Get currently active language code.
 * @return Current language based on active mode
 */
fun getCurrentLanguage(): String

/**
 * Get current language preference mode.
 * @return LanguageMode.SYSTEM or LanguageMode.USER_PREFERENCE
 */
fun getLanguageMode(): LanguageMode
```

**Usage Examples:**
```kotlin
// Switch to Spanish
Kloca.setUserLanguage("es")

// Back to system language
Kloca.useSystemLanguage()

// Check current state
val isSystemMode = Kloca.isUsingSystemLanguage()
val currentLanguage = Kloca.getCurrentLanguage()
```

### Language Information

```kotlin
/**
 * Get list of available language codes.
 * @return List of languages that have translations
 */
fun getAvailableLanguages(): List<String>

```

**Usage Examples:**
```kotlin
val languages = Kloca.getAvailableLanguages()
// → ["en", "es", "fr"]
```

### Validation & Status

```kotlin
/**
 * Check if a translation exists for given key.
 * @param key Translation key
 * @return true if translation exists, false otherwise
 */
fun hasTranslation(key: String): Boolean

/**
 * Check if Kloca has been initialized.
 * @return true if ready to use, false otherwise
 */
fun isReady(): Boolean

/**
 * Check if using system language mode.
 * @return true if using system language
 */
fun isUsingSystemLanguage(): Boolean

/**
 * Check if using user preference mode.
 * @return true if using user preference
 */
fun isUsingUserPreference(): Boolean
```

### Reactive API (Flows)

For reactive programming and UI updates:

```kotlin
/**
 * Flow that emits language mode changes.
 * @return Flow<LanguageMode>
 */
fun languageModeFlow(): Flow<LanguageMode>

/**
 * Flow that emits current language changes.
 * @return Flow<String> of language codes
 */
fun currentLanguageFlow(): Flow<String>

/**
 * Flow that combines mode and language state.
 * @return Flow<LanguageState>
 */
fun languageStateFlow(): Flow<LanguageState>
```

**Usage Examples:**
```kotlin
// Observe language changes
Kloca.currentLanguageFlow()
    .collect { language ->
        println("Language changed to: $language")
    }

// Observe complete state
Kloca.languageStateFlow()
    .collect { state ->
        println("Mode: ${state.mode}, Language: ${state.currentLanguage}")
    }
```

### Data Classes

```kotlin
/**
 * Language preference modes.
 */
enum class LanguageMode {
    /** Use system language (default) */
    SYSTEM,
    /** Use user-preferred language */
    USER_PREFERENCE
}

/**
 * Combined language state.
 */
data class LanguageState(
    val mode: LanguageMode,
    val currentLanguage: String
)
```

## Gradle Plugin API

Configuration DSL for the Gradle plugin.

### Plugin Application

```kotlin
plugins {
    kotlin("multiplatform")
    id("io.github.rlce.kloca") version "0.1.0"
}
```

### Runtime Dependencies

Add the runtime dependencies to your `commonMain` source set:

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                // Core runtime for string access
                implementation("io.github.rlce:kloca-runtime:0.1.0")

                // Optional: Compose integration for reactive UI
                implementation("io.github.rlce:kloca-runtime-compose:0.1.0")
            }
        }
    }
}
```

### Configuration DSL

```kotlin
kloca {
    /**
     * Language for Android default values/ folder.
     * Default: "en"
     */
    defaultLanguage = "es"

    /**
     * Prefix for generated StringKeys class.
     * Default: "" (generates "StringKeys")
     */
    namespacePrefix = "MyApp"  // generates "MyAppStringKeys"
}
```

### Generated Tasks

The plugin automatically creates these tasks:

#### generateTranslations
Processes YAML files and generates all resources.

```bash
./gradlew generateTranslations
```

**Dependencies:**
- Runs before `compileKotlin*` tasks
- Runs before Android resource merging
- Runs before iOS Compose resource tasks

## Compose Integration API

Reactive Compose functions with automatic recomposition.

### Core Functions

```kotlin
/**
 * Get localized string in Compose with automatic recomposition.
 * @param key Translation key
 * @return Localized string
 */
@Composable
fun localizedString(key: String): String

/**
 * Get formatted localized string in Compose.
 * @param key Translation key
 * @param args Format arguments
 * @return Formatted localized string
 */
@Composable
fun localizedString(key: String, vararg args: Any): String
```

### Extension Functions

```kotlin
/**
 * Extension for type-safe string access.
 * Usage: StringKeys.GREETING_HELLO.localized()
 */
@Composable
fun String.localized(): String

/**
 * Extension for formatted type-safe access.
 * Usage: StringKeys.GREETING_WELCOME.localized("John")
 */
@Composable
fun String.localized(vararg args: Any): String
```

### Reactive Hooks

```kotlin
/**
 * Composable hook for language change notifications.
 * @return Current language code
 */
@Composable
fun useLanguageChange(): String

/**
 * Composable hook for language mode changes.
 * @return Current language mode
 */
@Composable
fun useLanguageModeChange(): Kloca.LanguageMode
```

**Usage Examples:**
```kotlin
@Composable
fun MyScreen() {
    Column {
        // Direct access
        Text(localizedString("greeting.hello"))

        // With parameters
        Text(localizedString("greeting.welcome", "John"))

        // Type-safe access
        Text(StringKeys.GREETING_HELLO.localized())
        Text(StringKeys.GREETING_WELCOME.localized("John"))

        // Reactive language display
        val currentLanguage = useLanguageChange()
        Text("Current: $currentLanguage")
    }
}
```

## Generated Code API

### StringKeys Object

Auto-generated from YAML files:

```kotlin
/**
 * Generated string keys for type-safe access.
 * Regenerated on each build from YAML files.
 */
object StringKeys {
    const val GREETING_HELLO = "greeting.hello"
    const val GREETING_WELCOME = "greeting.welcome"
    const val NAVIGATION_HOME = "navigation.home"
    // ... more constants
}

// With namespace prefix "MyApp":
object MyAppStringKeys {
    // same structure
}
```

**Key Transformation Rules:**
- `greeting.hello` → `GREETING_HELLO`
- `user.profile.edit` → `USER_PROFILE_EDIT`
- `feature-auth.login` → `FEATURE_AUTH_LOGIN`

## Advanced APIs

### Direct Provider Access (Advanced)

For advanced use cases, direct access to underlying providers:

```kotlin
/**
 * System language provider (follows device settings).
 */
expect object SystemLanguageStringProvider {
    fun initialize(context: Any)
    fun get(key: String): String
    fun get(key: String, vararg args: Any): String
    fun getCurrentLanguage(): String
    fun clearCache()
}

/**
 * User language provider (user-selectable language).
 */
expect object UserLanguageStringProvider {
    /**
     * Initialize the provider with available languages and language persistence factory.
     * @param context Platform-specific context
     * @param availableLanguages Array of supported language codes
     * @param languagePersistenceFactory Factory to create LanguagePersistence instance for user language storage
     */
    fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory)
    fun setLanguage(languageCode: String)
    fun getCurrentLanguage(): String?
    fun getString(key: String): String
    fun getString(key: String, vararg args: Any): String
    fun hasTranslation(key: String): Boolean
    fun getAvailableLanguages(): List<String>
}
```

⚠️ **Note:** Direct provider access is for advanced scenarios. Most applications should use the unified `Kloca` object.

## Error Handling

### Return Values
- Missing translations return the key itself as fallback
- No exceptions thrown for missing keys
- Initialization errors may throw exceptions

### Validation
```kotlin
// Check if key exists
if (Kloca.hasTranslation("some.key")) {
    val text = Kloca.getString("some.key")
}

// Check initialization
if (!Kloca.isReady()) {
    Kloca.initialize(context)
}
```

## Platform Differences

### Android
- **Context Required:** Application context needed for initialization
- **Resources:** Reads from standard Android string resources
- **Storage:** SharedPreferences for user language persistence

### iOS
- **No Context:** Pass `Unit` for context parameter
- **Resources:** Reads from NSBundle .lproj files
- **Storage:** NSUserDefaults for user language persistence

## Migration Guide

### From Legacy StringProvider
```kotlin
// Old API
StringProvider.initialize(context)
val text = StringProvider.get("key")

// New API (recommended)
Kloca.initialize(
    context = context,
    languagePersistenceFactory = { /* platform-specific persistence implementation */ }
)
val text = Kloca.getString("key")
```

### From Direct Provider Access
```kotlin
// Old approach
SystemLanguageStringProvider.initialize(context)
val text = SystemLanguageStringProvider.get("key")

// New unified approach
Kloca.initialize(
    context = context,
    languagePersistenceFactory = { /* platform-specific persistence implementation */ }
)
val text = Kloca.getString("key")
```

## Best Practices

### Initialization
```kotlin
// ✅ Do: Initialize once in Application
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Kloca.initialize(
            context = this,
            availableLanguages = arrayOf("en", "es", "fr"),
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(this) }
        )
    }
}

// ❌ Don't: Initialize multiple times
// ❌ Don't: Initialize in Activities/ViewModels
```

### String Access
```kotlin
// ✅ Do: Use type-safe keys
val text = Kloca.getString(StringKeys.GREETING_HELLO)

// ✅ Do: Handle parameters properly
val welcome = Kloca.getString(StringKeys.USER_WELCOME, userName)

// ❌ Don't: Use raw strings (typo-prone)
val text = Kloca.getString("greeting.hello")
```

### Compose Integration
```kotlin
// ✅ Do: Use reactive functions
@Composable
fun MyScreen() {
    Text(StringKeys.TITLE.localized())
}

// ✅ Do: Use hooks for language-dependent logic
@Composable
fun LanguageDisplay() {
    val language = useLanguageChange()
    Text("Language: $language")
}

// ❌ Don't: Access Kloca directly in Compose without reactivity
@Composable
fun BadExample() {
    Text(Kloca.getString("title")) // Won't recompose on language change
}
```

This API documentation provides comprehensive coverage of all public interfaces in Kloca, with examples and best practices for effective usage.