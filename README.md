# Kloca - KSP i18n Plugin for Kotlin Multiplatform

[![kloca-gradle-plugin](https://img.shields.io/badge/kloca--gradle--plugin-0.1.0-blue?logo=gradle)](https://github.com/rlce/kloca/packages)
[![kloca-runtime](https://img.shields.io/badge/kloca--runtime-0.1.0-blue?logo=kotlin)](https://github.com/rlce/kloca/packages)
[![KMP](https://img.shields.io/badge/Platforms-Android%20%7C%20iOS-blue?logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.2.0+-blue.svg)](https://kotlinlang.org)

Kloca is a KSP (Kotlin Symbol Processing) Gradle plugin that automates internationalization (i18n) for Kotlin Multiplatform projects. It processes YAML translation files and generates platform-specific resources and type-safe Kotlin code.

## Overview

### The Challenge of Multiplatform Localization

Picture this: You're building a Kotlin Multiplatform app that needs to support multiple languages. Your team includes iOS and Android developers, designers, and translators. Each platform handles localization differently - Android uses XML string resources, iOS uses `.strings` files, and your shared business logic needs type-safe access to translations.

**Traditional approaches lead to:**
- ❌ **Duplicated translation files** across platforms
- ❌ **Manual synchronization** between iOS and Android resources  
- ❌ **Runtime string errors** from typos in key names
- ❌ **Translator confusion** with platform-specific formats
- ❌ **Broken builds** when translations are out of sync
- ❌ **Complex CI/CD** pipelines to manage multiple resource formats

### The Kloca Solution

Kloca transforms this complexity into simplicity with a **single source of truth** approach:

```yaml
# One YAML file for all platforms
i18n:
  onboarding:
    welcome: "Welcome to our app!"
    get_started: "Get Started"
    learn_more: "Learn more about %s"
```

**One command generates everything:**
```bash
./gradlew generateTranslations
```

**Result: Platform-native resources + type-safe code**

## Why Choose Kloca?

### 🎯 **Developer Experience First**
- **Write once, deploy everywhere**: Single YAML files become platform-native resources
- **Type-safe by design**: Generated constants prevent runtime string errors
- **IDE-friendly**: Full autocomplete and refactoring support
- **Zero configuration**: Works out of the box with sensible defaults

### 🏢 **Team Collaboration**
```yaml
# Translators work with familiar YAML
# Designers can easily reference keys  
# Developers get compile-time safety
i18n:
  user_profile:
    title: "Profile"
    edit_button: "Edit"
    save_changes: "Save Changes"
```

### 🚀 **Production Ready**
- **Battle-tested**: Generated code follows platform best practices
- **Performance optimized**: Compile-time generation, runtime efficiency
- **Fallback support**: Graceful handling of missing translations
- **Build integration**: Seamlessly fits into existing CI/CD workflows

### 🗂️ **Organized by Design**

Kloca's YAML tree structure naturally organizes translations by feature, module, and screen, making it incredibly easy to manage translations and understand where they're used:

```yaml
i18n:
  # Feature-based organization
  authentication:
    login:
      title: "Sign In"
      email_hint: "Email address"
      password_hint: "Password"
      forgot_password: "Forgot password?"
      sign_in_button: "Sign In"
    
    registration:
      title: "Create Account"
      terms_agreement: "I agree to the %s and %s"
      create_account_button: "Create Account"
  
  # Screen-based organization
  home:
    welcome_message: "Welcome back, %s!"
    quick_actions: "Quick Actions"
    recent_activity: "Recent Activity"
  
  # Module-based organization  
  payment:
    checkout:
      total: "Total: %s"
      pay_button: "Pay Now"
    history:
      title: "Payment History"
      empty_state: "No payments yet"
```

**Why This Matters:**

- **🎯 Clear Context**: `authentication.login.title` immediately tells you this is the login screen title
- **🔄 Easy Refactoring**: Moving features? Just restructure the YAML tree - keys update automatically
- **📋 Better Reviews**: Translators can see the context and relationship between strings
- **🔍 Quick Navigation**: Find all strings for a feature by navigating the YAML tree
- **🧩 Modular Flexibility**: Each feature module can maintain its own translation namespace

**Generated Type-Safe Keys:**
```kotlin
object StringKeys {
    const val AUTHENTICATION_LOGIN_TITLE = "authentication.login.title"
    const val AUTHENTICATION_LOGIN_EMAIL_HINT = "authentication.login.email_hint"
    const val HOME_WELCOME_MESSAGE = "home.welcome_message"
    const val PAYMENT_CHECKOUT_TOTAL = "payment.checkout.total"
}
```

This hierarchical approach makes your feature modules more **flexible and manageable** because:
- Related strings are grouped together logically
- Refactoring features doesn't break translation keys
- New team members can quickly understand the app structure through the YAML files
- Translation updates can be scoped to specific features or screens

## Features

- 🌍 **Multiplatform Support**: Works with Android and iOS targets
- 📝 **YAML-based Translations**: Simple, readable translation files
- 🔒 **Type-safe Access**: Generated string constants for compile-time safety
- 🎨 **Compose Integration**: Built-in Compose functions for easy UI localization
- 🚀 **Automatic Resource Generation**: Generates platform-specific resource files
- ⚡ **KSP Powered**: Fast, incremental code generation
- 🛡️ **Fallback Support**: Returns keys when translations are missing

## Quick Start

### 1. Apply the Plugin

Add to your `build.gradle.kts`:

```kotlin
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca.plugin") version "0.1.0"
}
```

### 2. Add Runtime Dependency

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("dev.rlce.kloca:runtime:0.1.0")
        }
    }
}
```

### 3. Create Translation Files

Create YAML files in your configured source directory:

**`src/main/kloca/i18n.en.yaml`**:
```yaml
i18n:
  greeting:
    hello: "Hello"
    goodbye: "Goodbye"
    welcome: "Welcome, %s!"
  navigation:
    home: "Home"
    settings: "Settings"
```

**`src/main/kloca/i18n.es.yaml`**:
```yaml
i18n:
  greeting:
    hello: "Hola"
    goodbye: "Adiós"
    welcome: "¡Bienvenido, %s!"
  navigation:
    home: "Inicio"
    settings: "Configuración"
```

### 4. Initialize in Your Application

#### Android Application Class
```kotlin
import dev.rlce.kloca.runtime.DefaultAndroidLanguagePersistence
import dev.rlce.kloca.runtime.Kloca

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize with system language only
        Kloca.initialize(
            context = this,
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(this) }
        )

        // OR initialize with user language selection support
        val availableLanguages = arrayOf("en", "es", "fr", "de")
        Kloca.initialize(
            context = this,
            availableLanguages = availableLanguages,
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(this) }
        )
    }
}
```

#### iOS Application Initialization
```kotlin
import dev.rlce.kloca.runtime.DefaultIosLanguagePersistence
import dev.rlce.kloca.runtime.Kloca

// In your shared KMP initialization code
fun initializeApp() {
    // Initialize with system language only
    Kloca.initialize(
        context = Unit,
        languagePersistenceFactory = { DefaultIosLanguagePersistence() }
    )

    // OR initialize with user language selection support
    val availableLanguages = arrayOf("en", "es", "fr", "de")
    Kloca.initialize(
        context = Unit,
        availableLanguages = availableLanguages,
        languagePersistenceFactory = { DefaultIosLanguagePersistence() }
    )
}
```

### 5. Use in Your Code

#### Direct Access
```kotlin
val greeting = Kloca.getString("greeting.hello")
val welcome = Kloca.getString("greeting.welcome", "John")
```

#### Type-safe Access (Recommended)
```kotlin
val greeting = Kloca.getString(StringKeys.GREETING_HELLO)
val welcome = Kloca.getString(StringKeys.GREETING_WELCOME, "John")
```

#### In Compose
```kotlin
@Composable
fun GreetingScreen() {
    Column {
        Text(localizedString("greeting.hello"))
        Text(localizedString(StringKeys.GREETING_WELCOME, "John"))

        // Or using extension functions
        Text(StringKeys.GREETING_HELLO.localized())
        Text(StringKeys.GREETING_WELCOME.localized("John"))
    }
}
```

#### User Language Selection
```kotlin
// Check available languages
val languages = Kloca.getAvailableLanguages()
println("Available: ${languages.joinToString()}")

// Set user preferred language
Kloca.setUserLanguage("es")

// Switch back to system language
Kloca.useSystemLanguage()

// Check current mode
val isSystemMode = Kloca.isUsingSystemLanguage()
val currentLang = Kloca.getCurrentLanguage()
```

## Platform Integration

### Android Integration

#### 1. Plugin Configuration

In your shared module's `build.gradle.kts`:

```kotlin
plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("dev.rlce.kloca.plugin") version "0.1.0"
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("dev.rlce.kloca:runtime:0.1.0")
        }
    }
}
```

#### 2. Application Setup

Create or update your `Application` class:

```kotlin
// android/src/main/kotlin/com/yourapp/MyApplication.kt
import android.app.Application
import dev.rlce.kloca.runtime.DefaultAndroidLanguagePersistence
import dev.rlce.kloca.runtime.Kloca

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Kloca with application context and language persistence factory
        Kloca.initialize(
            context = this,
            languagePersistenceFactory = { DefaultAndroidLanguagePersistence(this) }
        )
    }
}
```

#### 3. Gradle Task Integration

The plugin automatically hooks into the Android build process. You can also run generation manually:

```bash
# Generate translations for Android
./gradlew generateTranslations

# Build with fresh translations
./gradlew clean build
```

#### 4. Generated Android Resources

Kloca generates standard Android string resources:

```
android/
├── src/main/res/
│   ├── values/strings.xml              # Default language (en)
│   ├── values-es/strings.xml           # Spanish
│   ├── values-fr/strings.xml           # French
│   └── values-de/strings.xml           # German
```

#### 5. Usage in Android Activities/Fragments

```kotlin
// In Activities or Fragments
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Direct usage
        val greeting = Kloca.getString("greeting.hello")

        // Type-safe usage
        val title = Kloca.getString(StringKeys.NAVIGATION_HOME)

        // With parameters
        val welcome = Kloca.getString(StringKeys.GREETING_WELCOME, "John")

        setContent {
            MyAppTheme {
                GreetingScreen()
            }
        }
    }
}
```

#### 6. Jetpack Compose Integration

```kotlin
@Composable
fun GreetingScreen() {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Direct string access
        Text(
            text = localizedString("greeting.hello"),
            style = MaterialTheme.typography.headlineMedium
        )
        
        // Type-safe access
        Text(
            text = StringKeys.GREETING_WELCOME.localized("User"),
            style = MaterialTheme.typography.bodyLarge
        )
        
        // With formatting
        Text(
            text = localizedString("greeting.welcome", "John Doe"),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

#### 7. Locale Changes

Kloca automatically responds to system locale changes:

### iOS Integration

#### 1. Plugin Configuration

The same configuration in your shared module works for iOS:

```kotlin
// In your shared module's build.gradle.kts
kotlin {
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    
    sourceSets {
        iosMain.dependencies {
            implementation("dev.rlce.kloca:runtime:0.1.0")
        }
    }
}
```

#### 2. Automatic Resource Generation

Kloca automatically generates localization resources in `iosMain/resources/` with proper bundle structure:

```
shared/src/iosMain/resources/
├── en.lproj/Localizable.strings
├── es.lproj/Localizable.strings
├── fr.lproj/Localizable.strings
└── de.lproj/Localizable.strings
```

The improved iOS StringProvider automatically detects and loads these resources using NSBundle - **no manual Xcode setup required!**

#### 3. Automatic Language Detection

The iOS StringProvider automatically:
- Detects the user's system language (`NSLocale.currentLocale.languageCode`)
- Resolves the appropriate `.lproj` bundle for that language
- Falls back to the main bundle if a specific language bundle isn't found
- Works seamlessly with iOS's localization system

#### 4. Swift Integration

Use the generated translations in Swift:

```swift
// ViewController.swift
import UIKit
import shared // Your KMP shared module

class ViewController: UIViewController {
    @IBOutlet weak var greetingLabel: UILabel!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Using Kloca from KMP
        greetingLabel.text = Kloca.shared.getString(key: "greeting.hello")

        // Type-safe access
        let title = Kloca.shared.getString(key: StringKeys.shared.NAVIGATION_HOME)
        self.title = title

        // With parameters
        let welcome = Kloca.shared.getString(key: StringKeys.shared.GREETING_WELCOME, args: ["John"])
        print(welcome)
    }
}
```

#### 5. SwiftUI Integration

```swift
// SwiftUI View
import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        VStack(spacing: 20) {
            Text(Kloca.shared.getString(key: "greeting.hello"))
                .font(.largeTitle)

            Text(Kloca.shared.getString(key: StringKeys.shared.GREETING_WELCOME, args: ["SwiftUI"]))
                .font(.body)

            Button(Kloca.shared.getString(key: StringKeys.shared.NAVIGATION_HOME)) {
                // Action
            }
        }
        .padding()
    }
}
```

## Generated Code

The plugin generates several artifacts:

### StringKeys.kt
```kotlin
object StringKeys {
    const val GREETING_HELLO = "greeting.hello"
    const val GREETING_GOODBYE = "greeting.goodbye"
    const val GREETING_WELCOME = "greeting.welcome"
    const val NAVIGATION_HOME = "navigation.home"
    const val NAVIGATION_SETTINGS = "navigation.settings"
}
```

### Android Resources
```xml
<!-- values/strings.xml -->
<resources>
    <string name="greeting_hello">Hello</string>
    <string name="greeting_goodbye">Goodbye</string>
    <string name="greeting_welcome">Welcome, %s!</string>
    <string name="navigation_home">Home</string>
    <string name="navigation_settings">Settings</string>
</resources>
```

### iOS Resources
```
/* en.lproj/Localizable.strings */
"greeting.hello" = "Hello";
"greeting.goodbye" = "Goodbye";
"greeting.welcome" = "Welcome, %s!";
"navigation.home" = "Home";
"navigation.settings" = "Settings";
```

## Configuration

The plugin works with minimal configuration. Most settings use opinionated defaults:

```kotlin
kloca {
    defaultLanguage = "en"        // Default language (default: "en")
    namespacePrefix = "MyApp"     // Class prefix (default: project name)
}
```

### Understanding `defaultLanguage`

**✅ What `defaultLanguage` DOES:**
- **Android Resources**: Controls which language goes in the default `values/strings.xml` folder
- **Resource Organization**: Other languages go into `values-<lang>/strings.xml` folders
- **Build Integration**: Affects how Android's resource system resolves strings

**❌ What `defaultLanguage` DOES NOT do:**
- **Runtime Fallback**: Does not control runtime string fallback logic
- **Device Language**: Does not override the user's device language settings
- **StringProvider Logic**: Does not affect which strings StringProvider.get() returns

**Example with Spanish as default:**
```kotlin
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca.plugin") version "0.1.0"
}

kloca {
    defaultLanguage = "es"        // Spanish in Android values/ folder
    namespacePrefix = "MyApp"     // Generates MyAppStringKeys class
}
```

**Generated Android Resources:**
```
android/res/
├── values/strings.xml              ← Spanish content (default)
├── values-en/strings.xml           ← English content  
└── values-fr/strings.xml           ← French content
```

**Generated iOS Resources (unchanged by defaultLanguage):**
```
shared/src/iosMain/resources/
├── es.lproj/Localizable.strings    ← Spanish
├── en.lproj/Localizable.strings    ← English
└── fr.lproj/Localizable.strings    ← French
```

### Understanding `namespacePrefix`

**✅ What `namespacePrefix` DOES:**
- **Class Naming**: Controls the generated StringKeys class name
- **Multi-Module Organization**: Helps avoid naming conflicts between modules
- **Code Generation**: Affects generated Kotlin class names

**Examples:**
```kotlin
// Default (uses project name)
namespacePrefix = ""           // → StringKeys (or ProjectStringKeys)

// Custom prefix
namespacePrefix = "MyApp"      // → MyAppStringKeys
namespacePrefix = "Auth"       // → AuthStringKeys
namespacePrefix = "Profile"    // → ProfileStringKeys
```

**Generated Code:**
```kotlin
// With namespacePrefix = "MyApp"
object MyAppStringKeys {
    const val GREETING_HELLO = "greeting.hello"
    const val NAVIGATION_HOME = "navigation.home"
}
```

## YAML Structure

Kloca supports nested YAML structures with dot notation flattening:

```yaml
i18n:
  feature:
    screen:
      title: "Screen Title"
      button: "Click Me"
    dialog:
      message: "Are you sure?"
      confirm: "Yes"
      cancel: "No"
```

This generates keys like:
- `feature.screen.title`
- `feature.screen.button`
- `feature.dialog.message`
- etc.

## Runtime-Only Usage (Without Gradle Plugin)

If you prefer to manage your translation files manually or use a different build system, you can use Kloca runtime libraries independently:

### 1. Add Runtime Dependencies Only

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            // Core runtime for string access
            implementation("dev.rlce.kloca:runtime:0.1.0")

            // Optional: Compose integration
            implementation("dev.rlce.kloca:runtime-compose:0.1.0")
        }
    }
}
```

### 2. Manual Resource Management

#### For Android
Place your translation files in standard Android resources:
```
android/src/main/res/
├── values/strings.xml              # Default language
├── values-es/strings.xml           # Spanish
└── values-fr/strings.xml           # French
```

Example `values/strings.xml`:
```xml
<resources>
    <string name="greeting_hello">Hello</string>
    <string name="greeting_welcome">Welcome, %s!</string>
    <string name="navigation_home">Home</string>
</resources>
```

#### For iOS
Place your translation files in standard iOS localization structure:
```
shared/src/iosMain/resources/
├── en.lproj/Localizable.strings
├── es.lproj/Localizable.strings
└── fr.lproj/Localizable.strings
```

Example `en.lproj/Localizable.strings`:
```
"greeting.hello" = "Hello";
"greeting.welcome" = "Welcome, %s!";
"navigation.home" = "Home";
```

### 3. Manual StringKeys Generation

Create your own StringKeys object for type safety:

```kotlin
// src/commonMain/kotlin/StringKeys.kt
object StringKeys {
    const val GREETING_HELLO = "greeting.hello"
    const val GREETING_WELCOME = "greeting.welcome"
    const val NAVIGATION_HOME = "navigation.home"
}
```

### 4. Initialize and Use

```kotlin
// Initialize Kloca in your application
Kloca.initialize(
    context = context,
    languagePersistenceFactory = { /* platform-specific persistence implementation */ }
)
// or with user language selection support:
Kloca.initialize(
    context = context,
    availableLanguages = supportedLanguages,
    languagePersistenceFactory = { /* platform-specific persistence implementation */ }
)

// Use in your code
val greeting = Kloca.getString(StringKeys.GREETING_HELLO)
val welcome = Kloca.getString(StringKeys.GREETING_WELCOME, "John")

// In Compose (if using compose runtime)
@Composable
fun MyScreen() {
    Text(StringKeys.GREETING_HELLO.localized())
}
```

### 5. When to Use Runtime-Only

✅ **Good for:**
- Legacy projects with existing resource management
- Custom build systems or CI/CD pipelines
- Projects requiring fine-grained control over resource generation
- Integration with external translation management systems

❌ **Not recommended when:**
- You want automatic resource synchronization across platforms
- You need YAML-based translation files
- You want compile-time validation of translation consistency

## Advanced Usage

### Multi-Module Projects

For projects with multiple modules, simply apply the plugin to each module:

```kotlin
// Module 1: feature-auth
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca.plugin")
}

// Module 2: feature-profile  
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca.plugin")
}
```

Each module generates its own `StringKeys` class with project-based naming:
```kotlin
object AuthStringKeys { ... }    // Generated for auth module
object ProfileStringKeys { ... } // Generated for profile module
```

### Parameterized Translations

Support for string formatting:

```yaml
i18n:
  messages:
    welcome: "Welcome, %s!"
    item_count: "You have %d items"
    progress: "Progress: %d%%"
    user_info: "User: %s, Age: %d, Email: %s"
```

Usage:
```kotlin
// Single parameter
Kloca.getString("messages.welcome", "John")

// Multiple parameters
Kloca.getString("messages.user_info", "John", 25, "john@example.com")
```

## Error Handling & Troubleshooting

### Common Issues

#### Missing Translations
**Symptom**: Key returned instead of translated text
**Solution**: 
- Check YAML file exists for the current locale
- Verify key exists in YAML file
- Ensure proper YAML structure

#### Build Failures
**Symptom**: `YAML parsing failed`
**Solution**:
- Check for common issues:
  - Tabs instead of spaces
  - Missing quotes around special characters
  - Incorrect indentation
  - Missing `i18n:` root key in YAML files

#### iOS Strings Not Loading
**Symptom**: English text shows instead of localized
**Solution**:
1. Run `./gradlew generateTranslations` to ensure resources are generated in `iosMain/resources/`
2. Verify that `.lproj` folders exist in `shared/src/iosMain/resources/`
3. Check that your KMP framework is properly linked to the iOS app

#### Android Resource Conflicts
**Symptom**: `Duplicate resource` errors
**Solution**:
- Check for conflicting resource names in your project
- Ensure unique naming for generated resources

### Debugging

Check generated files:
```bash
# View generated resources
find . -name "*generated*" -type d | grep kloca

# View generated StringKeys
cat build/generated/kloca/StringKeys.kt
```

## Building the Project

```bash
# Build all modules
./gradlew build

# Run tests
./gradlew test

# Generate translations (if you have a sample project configured)
./gradlew generateTranslations
```

## Sample Project

The `sample` directory contains a complete example showing both usage patterns:
- Direct StringProvider access
- Type-safe StringKeys usage
- Compose integration

## Requirements

- Kotlin 2.2.0+
- Gradle 8.0+
- KSP 2.2.0-2.0.2+
- Android Gradle Plugin 8.0+ (for Android targets)

## Documentation

- [API Documentation](API.md)
- [Changelog](CHANGELOG.md)

## Support

- [GitHub Issues](https://github.com/rlce/kloca/issues) - Bug reports and feature requests
- [GitHub Discussions](https://github.com/rlce/kloca/discussions) - Questions and community support

## Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details on:

- How to submit bug reports and feature requests
- Development setup and workflow
- Code style and testing requirements
- Pull request process

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [Kotlin Symbol Processing (KSP)](https://github.com/google/ksp) for enabling fast code generation
- [SnakeYAML](https://bitbucket.org/snakeyaml/snakeyaml/src/master/) for YAML processing
- [KotlinPoet](https://github.com/square/kotlinpoet) for Kotlin code generation