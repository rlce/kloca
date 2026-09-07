# Kloca Runtime

The core runtime library for accessing localized strings in Kotlin Multiplatform projects. Provides platform-specific implementations for Android, iOS, and WasmJs with support for both system language and user preference modes.

## Overview

Kloca Runtime offers two main providers:

1. **SystemLanguageStringProvider**: Automatically follows system language settings
2. **UserLanguageStringProvider**: Allows users to override system language
3. **Kloca Object**: Unified API that manages both providers intelligently

## Quick Start

### 1. Add Dependency

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.rlce:kloca-runtime:0.2.0")
        }
    }
}
```

### 2. Initialize

```kotlin
// Android - System language only
Kloca.initialize(
    context = context,
    languagePersistenceFactory = { DefaultAndroidLanguagePersistence(context) }
)

// Android - With user language selection support
val availableLanguages = arrayOf("en", "es", "fr", "de")
Kloca.initialize(
    context = context,
    availableLanguages = availableLanguages,
    languagePersistenceFactory = { DefaultAndroidLanguagePersistence(context) }
)

// iOS - System language only
Kloca.initialize(
    context = Unit,
    languagePersistenceFactory = { DefaultIosLanguagePersistence() }
)

// iOS - With user language selection support
Kloca.initialize(
    context = Unit,
    availableLanguages = availableLanguages,
    languagePersistenceFactory = { DefaultIosLanguagePersistence() }
)
```

### 3. Use

```kotlin
// Get localized strings
val greeting = Kloca.getString("greeting.hello")
val welcome = Kloca.getString("greeting.welcome", "John")

// Language management
Kloca.setUserLanguage("es")       // Switch to Spanish
Kloca.useSystemLanguage()         // Back to system language
val current = Kloca.getCurrentLanguage() // Current language code
```

## API Reference

### Kloca Object (Recommended)

The main API that intelligently manages both system and user language preferences:

```kotlin
object Kloca {
    // Initialization
    fun initialize(context: Any, languagePersistenceFactory: LanguagePersistenceFactory)
    fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory)

    // String access
    fun getString(key: String): String
    fun getString(key: String, vararg args: Any): String

    // Language management
    fun setUserLanguage(languageCode: String)
    fun useSystemLanguage()
    fun getCurrentLanguage(): String
    fun getLanguageMode(): LanguageMode

    // Availability checks
    fun hasTranslation(key: String): Boolean
    fun getAvailableLanguages(): List<String>

    // Status
    fun isReady(): Boolean
    fun isUsingSystemLanguage(): Boolean
    fun isUsingUserPreference(): Boolean

    // Reactive API (Flows)
    fun languageModeFlow(): Flow<LanguageMode>
    fun currentLanguageFlow(): Flow<String>
    fun languageStateFlow(): Flow<LanguageState>
}
```

### SystemLanguageStringProvider (Advanced)

Direct access to system language provider:

```kotlin
expect object SystemLanguageStringProvider {
    fun initialize(context: Any)
    fun get(key: String): String
    fun get(key: String, vararg args: Any): String
    fun getCurrentLanguage(): String
    fun clearCache()
}
```

### UserLanguageStringProvider (Advanced)

Direct access to user preference provider:

```kotlin
expect object UserLanguageStringProvider {
    fun initialize(context: Any, availableLanguages: Array<String>, languagePersistenceFactory: LanguagePersistenceFactory)
    fun setLanguage(languageCode: String)
    fun getCurrentLanguage(): String?
    fun getString(key: String): String
    fun getString(key: String, vararg args: Any): String
    fun hasTranslation(key: String): Boolean
    fun getAvailableLanguages(): List<String>
}
```

## Language Modes

Kloca supports two language modes:

### SYSTEM Mode (Default)
- Automatically follows device system language
- Changes when user changes device language
- No persistent storage needed

### USER_PREFERENCE Mode
- Uses explicitly set user language
- Persists across app restarts
- Independent of system language changes

## Platform-Specific Behavior

### WasmJs

Pass the plugin-generated `<Namespace>Translations` object as the context. The runtime
uses the browser locale for system language and `localStorage` for persistence:

```kotlin
startKloca(
    context = SampleTranslations,
    availableLanguages = SampleTranslations.translations.keys.toTypedArray(),
    languagePersistenceFactory = { DefaultWasmLanguagePersistence() },
)
```

### Android
- **Context**: Requires `Application` context for initialization
- **Resources**: Reads from standard Android string resources
- **Storage**: Uses SharedPreferences for user language persistence
- **Language Detection**: Uses `Locale.getDefault()`

```kotlin
// In Application.onCreate()
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
```

### iOS
- **Context**: Uses `Unit` (no context needed)
- **Resources**: Reads from NSBundle localization files (`.lproj`)
- **Storage**: Uses NSUserDefaults for user language persistence
- **Language Detection**: Uses `NSLocale.currentLocale().languageCode`

```kotlin
// In shared initialization
fun initializeApp() {
    Kloca.initialize(
        context = Unit,
        availableLanguages = arrayOf("en", "es", "fr"),
        languagePersistenceFactory = { DefaultIosLanguagePersistence() }
    )
}
```

## Resource File Requirements

### Android
Standard Android string resources in `res/` directory:

```
res/
├── values/strings.xml          # Default/fallback language
├── values-es/strings.xml       # Spanish
├── values-fr/strings.xml       # French
└── values-de/strings.xml       # German
```

Example `values/strings.xml`:
```xml
<resources>
    <string name="greeting_hello">Hello</string>
    <string name="greeting_welcome">Welcome, %1$s!</string>
</resources>
```

### iOS
Standard iOS localization bundles in `iosMain/resources/`:

```
iosMain/resources/
├── en.lproj/Localizable.strings
├── es.lproj/Localizable.strings
├── fr.lproj/Localizable.strings
└── de.lproj/Localizable.strings
```

Example `en.lproj/Localizable.strings`:
```
"greeting.hello" = "Hello";
"greeting.welcome" = "Welcome, %1$@!";
```

## Reactive Programming Support

Kloca provides Flow-based reactive API for observing language changes:

```kotlin
// Observe language mode changes
Kloca.languageModeFlow()
    .collect { mode ->
        println("Language mode: $mode")
    }

// Observe current language changes
Kloca.currentLanguageFlow()
    .collect { language ->
        println("Current language: $language")
    }

// Observe complete language state
Kloca.languageStateFlow()
    .collect { state ->
        println("Mode: ${state.mode}, Language: ${state.currentLanguage}")
    }
```

## Advanced Usage

### Language Information

Get available language information:

```kotlin
// Display language codes directly
val currentLanguage = Kloca.getCurrentLanguage()
println("Current language: $currentLanguage")

// Get all available languages
val availableLanguages = Kloca.getAvailableLanguages()
// Output: ["en", "es", "fr"]
```

### Fallback Behavior

When a translation is missing:
1. Returns the key itself as fallback
2. No exceptions thrown
3. Logs warning for debugging

```kotlin
val missing = Kloca.getString("nonexistent.key")
// Returns: "nonexistent.key"
```

### Cache Management

```kotlin
// Clear platform-specific caches (advanced usage)
SystemLanguageStringProvider.clearCache()
```

## Usage Patterns

### Basic App with System Language Only
```kotlin
// Initialize once in application
Kloca.initialize(
    context = applicationContext,
    languagePersistenceFactory = { DefaultAndroidLanguagePersistence(applicationContext) }
)

// Use throughout app
val title = Kloca.getString("screen.title")
val message = Kloca.getString("welcome.message", userName)
```

### App with User Language Selection
```kotlin
// Initialize with available languages
Kloca.initialize(
    context = applicationContext,
    availableLanguages = arrayOf("en", "es", "fr", "de"),
    languagePersistenceFactory = { DefaultAndroidLanguagePersistence(applicationContext) }
)

// Provide language selection UI
val languages = Kloca.getAvailableLanguages()
languages.forEach { language ->
    val languageCode = language
    // Show in UI: language code directly like "EN", "ES", "FR"
}

// Handle user selection
fun onLanguageSelected(languageCode: String) {
    Kloca.setUserLanguage(languageCode)
    // App will immediately use new language
}

// Option to reset to system language
fun useSystemLanguage() {
    Kloca.useSystemLanguage()
}
```

### Reactive UI Updates
```kotlin
// In your UI layer (with Compose integration)
@Composable
fun LanguageAwareScreen() {
    val currentLanguage by Kloca.currentLanguageFlow()
        .collectAsState(Kloca.getCurrentLanguage())

    // UI automatically updates when language changes
    Text(Kloca.getString("dynamic.content"))
}
```

## Error Handling

### Common Issues

**"Kloca not initialized"**
- Call `Kloca.initialize()` before first use
- Check initialization happens on main thread

**"Translation key not found"**
- Verify resource files contain the key
- Check resource files are properly bundled
- Use `Kloca.hasTranslation(key)` to verify availability

**"Language not available"**
- Ensure resource files exist for the language
- Check language code format (e.g., "en", "es-ES")
- Verify bundles are included in build

### Debugging

```kotlin
// Check initialization status
if (!Kloca.isReady()) {
    println("Kloca not initialized!")
}

// Check available languages
val available = Kloca.getAvailableLanguages()
println("Available languages: $available")

// Check current state
println("Current language: ${Kloca.getCurrentLanguage()}")
println("Using system language: ${Kloca.isUsingSystemLanguage()}")
println("Available languages count: ${available.size}")

// Test specific key
val hasKey = Kloca.hasTranslation("test.key")
println("Has 'test.key': $hasKey")
```

## Performance Considerations

- **Lazy Loading**: Resources loaded on-demand per language
- **Caching**: Strings cached after first access
- **Memory Efficient**: Only active language resources kept in memory
- **Thread Safe**: All operations are thread-safe

## Migration from Legacy APIs

If migrating from older string provider APIs:

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

The old APIs (`SystemLanguageStringProvider`, `UserLanguageStringProvider`) remain available for advanced use cases but `Kloca` is recommended for most applications.

## Requirements

- Kotlin 2.2.0+
- Kotlinx Coroutines 1.8.1+ (for Flow support)
- Android API 24+ (for Android targets)
- iOS 13.0+ (for iOS targets)
