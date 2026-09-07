# Kloca Sample Project

A complete example demonstrating how to use Kloca for internationalization in a Kotlin Multiplatform project with Compose UI.

## Features Demonstrated

This sample showcases:

- ✅ **Gradle Plugin Integration**: Complete setup with YAML processing
- ✅ **Multi-language Support**: English and Spanish translations
- ✅ **Type-safe String Access**: Generated StringKeys usage
- ✅ **Compose Integration**: Reactive UI updates on language change
- ✅ **User Language Selection**: Runtime language switching
- ✅ **Platform-specific Initialization**: Android, iOS, and WasmJs setup
- ✅ **String Formatting**: Parameters in translations

Run the browser sample with `./gradlew :sample:wasmJsBrowserDevelopmentRun`, or
build its distribution with `./gradlew :sample:wasmJsBrowserDistribution`.

## Project Structure

```
sample/
├── src/
│   ├── main/kloca/                    # Translation files
│   │   ├── i18n.en.yaml              # English translations
│   │   └── i18n.es.yaml              # Spanish translations
│   ├── commonMain/kotlin/             # Shared code
│   │   ├── SampleApp.kt              # Main Compose app
│   │   ├── MainScreen.kt             # Demo screen
│   │   └── LanguageSelectionScreen.kt # Language picker
│   ├── androidMain/kotlin/            # Android-specific code
│   │   ├── MyApplication.kt          # App initialization
│   │   └── MainActivity.kt           # Android entry point
│   └── iosMain/kotlin/                # iOS-specific code
│       └── MainViewController.kt      # iOS entry point
├── build/                             # Generated files
│   └── generated/kloca/
│       └── StringKeys.kt             # Type-safe string constants
└── build.gradle.kts                  # Plugin configuration
```

## Translation Files

### English (i18n.en.yaml)
```yaml
i18n:
  app:
    title: "Kloca Sample"
    description: "Demonstrating Kotlin Multiplatform i18n"

  welcome:
    greeting: "Hello!"
    message: "Welcome to Kloca, {0}!"
    subtitle: "Select your preferred language below"

  language:
    title: "Language Selection"
    system_language: "Use System Language"
    current: "Current: {0}"
    available: "Available Languages"

  buttons:
    continue: "Continue"
    back: "Back"
    select: "Select"
    reset: "Reset to System"

  navigation:
    home: "Home"
    settings: "Settings"
    language_settings: "Language"
```

### Spanish (i18n.es.yaml)
```yaml
i18n:
  app:
    title: "Ejemplo de Kloca"
    description: "Demostrando i18n en Kotlin Multiplatform"

  welcome:
    greeting: "¡Hola!"
    message: "¡Bienvenido a Kloca, {0}!"
    subtitle: "Selecciona tu idioma preferido abajo"

  language:
    title: "Selección de Idioma"
    system_language: "Usar Idioma del Sistema"
    current: "Actual: {0}"
    available: "Idiomas Disponibles"

  buttons:
    continue: "Continuar"
    back: "Atrás"
    select: "Seleccionar"
    reset: "Restablecer al Sistema"

  navigation:
    home: "Inicio"
    settings: "Configuración"
    language_settings: "Idioma"
```

## Generated Code

The plugin automatically generates `StringKeys.kt`:

```kotlin
object sampleStringKeys {
    const val APP_TITLE = "app.title"
    const val APP_DESCRIPTION = "app.description"
    const val WELCOME_GREETING = "welcome.greeting"
    const val WELCOME_MESSAGE = "welcome.message"
    const val WELCOME_SUBTITLE = "welcome.subtitle"
    const val LANGUAGE_TITLE = "language.title"
    const val LANGUAGE_SYSTEM_LANGUAGE = "language.system_language"
    const val LANGUAGE_CURRENT = "language.current"
    const val LANGUAGE_AVAILABLE = "language.available"
    const val BUTTONS_CONTINUE = "buttons.continue"
    const val BUTTONS_BACK = "buttons.back"
    const val BUTTONS_SELECT = "buttons.select"
    const val BUTTONS_RESET = "buttons.reset"
    const val NAVIGATION_HOME = "navigation.home"
    const val NAVIGATION_SETTINGS = "navigation.settings"
    const val NAVIGATION_LANGUAGE_SETTINGS = "navigation.language_settings"
}
```

## Key Implementation Examples

### Application Initialization

#### Android (`MyApplication.kt`)
```kotlin
import dev.rlce.kloca.runtime.DefaultAndroidLanguagePersistence
import dev.rlce.kloca.runtime.startKloca

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Kloca with supported languages and language persistence factory
        startKloca(
            context = this,
            availableLanguages = arrayOf("en", "es"),
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(this) }
        )
    }
}
```

#### iOS (`MainViewController.kt`)
```kotlin
import dev.rlce.kloca.runtime.DefaultIosLanguagePersistence
import dev.rlce.kloca.runtime.startKloca

fun MainViewController(): UIViewController {
    startKloca(
        context = Unit,
        availableLanguages = arrayOf("en", "es"),
        languagePersistenceFactory = { DefaultIosLanguagePersistence() }
    )

    return ComposeUIViewController {
        SampleApp()
    }
}
```

### Main Screen (`MainScreen.kt`)
```kotlin
@Composable
fun MainScreen(onLanguageSettingsClick: () -> Unit) {
    val currentLanguage = useLanguageChange()
    val languageDisplay = currentLanguage.uppercase()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App title with reactive updates
        Text(
            text = sampleStringKeys.APP_TITLE.localized(),
            style = MaterialTheme.typography.displayMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App description
        Text(
            text = sampleStringKeys.APP_DESCRIPTION.localized(),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Welcome message with parameter
        Text(
            text = sampleStringKeys.WELCOME_MESSAGE.localized("User"),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Current language display
        Text(
            text = sampleStringKeys.LANGUAGE_CURRENT.localized(displayName),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Navigation button
        Button(onClick = onLanguageSettingsClick) {
            Text(sampleStringKeys.NAVIGATION_LANGUAGE_SETTINGS.localized())
        }
    }
}
```

### Language Selection (`LanguageSelectionScreen.kt`)
```kotlin
@Composable
fun LanguageSelectionScreen(onBack: () -> Unit) {
    val currentLanguage = useLanguageChange()
    val availableLanguages = remember { Kloca.getAvailableLanguages() }
    val isUsingSystem = Kloca.isUsingSystemLanguage()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = sampleStringKeys.BUTTONS_BACK.localized())
            }

            Text(
                text = sampleStringKeys.LANGUAGE_TITLE.localized(),
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // System language toggle
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(sampleStringKeys.LANGUAGE_SYSTEM_LANGUAGE.localized())
                Switch(
                    checked = isUsingSystem,
                    onCheckedChange = { useSystem ->
                        if (useSystem) {
                            Kloca.useSystemLanguage()
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Available languages
        Text(
            text = sampleStringKeys.LANGUAGE_AVAILABLE.localized(),
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(availableLanguages) { languageCode ->
                LanguageItem(
                    languageCode = languageCode,
                    isSelected = languageCode == currentLanguage,
                    enabled = !isUsingSystem,
                    onSelect = {
                        Kloca.setUserLanguage(languageCode)
                    }
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    languageCode: String,
    isSelected: Boolean,
    enabled: Boolean,
    onSelect: () -> Unit
) {
    val displayName = languageCode.uppercase()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = enabled) { onSelect() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = languageCode,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
```

## Running the Sample

### 1. Build and Generate Translations
```bash
./gradlew :sample:generateTranslations
./gradlew :sample-android-app:assembleDebug
```

### 2. Run on Android
```bash
./gradlew :sample-android-app:installDebug
```

### 3. Run on iOS
Open in Xcode and build the `iosApp` target.

## Key Learning Points

### 1. Reactive UI Updates
Notice how the UI automatically updates when language changes:
- `useLanguageChange()` provides reactive language state
- All `localized()` functions automatically recompose
- No manual UI refresh needed

### 2. Type Safety
The generated `StringKeys` object provides:
- Compile-time safety (no typos in string keys)
- IDE autocomplete and refactoring support
- Easy navigation to string definitions

### 3. String Parameters
Parameters are handled seamlessly:
```kotlin
// YAML: "Welcome, {0}!"
// Usage:
Text(sampleStringKeys.WELCOME_MESSAGE.localized("John"))
// Result: "Welcome, John!" or "¡Bienvenido, John!"
```

### 4. Platform Integration
Each platform initializes appropriately:
- Android: Uses Application context
- iOS: Uses Unit (no context needed)
- Resources automatically bundled correctly

## Customization Ideas

### Add More Languages
1. Create new YAML files (e.g., `i18n.fr.yaml`)
2. Add language to initialization: `arrayOf("en", "es", "fr")`
3. Rebuild to generate resources

### Complex Formatting
```yaml
i18n:
  order:
    summary: "Order #{0}: {1} items for {2}"

# Usage:
Text(StringKeys.ORDER_SUMMARY.localized(12345, 3, "$29.99"))
```

### Nested Organization
```yaml
i18n:
  features:
    auth:
      login:
        title: "Sign In"
        email_hint: "Email"
      register:
        title: "Create Account"
    profile:
      edit: "Edit Profile"
      save: "Save Changes"
```

### Testing Different Scenarios
The sample provides a perfect testing ground for:
- Language switching while app is running
- System language changes
- Resource fallback behavior
- Performance with many strings
- UI layout changes with different text lengths

This sample serves as both a demonstration and a starting point for implementing Kloca in your own projects.
