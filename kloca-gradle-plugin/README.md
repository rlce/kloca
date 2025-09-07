# Kloca Gradle Plugin

The Gradle plugin for Kloca that automates translation file processing and resource generation for Kotlin Multiplatform projects.

## Overview

This plugin processes YAML translation files and automatically generates:
- Platform-specific resource files (Android XML, iOS .strings)
- Type-safe Kotlin string constants (`StringKeys.kt`)
- Proper source set integration

## Usage

### 1. Apply the Plugin

```kotlin
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca") version "0.1.0"
}
```

### 2. Add Runtime Dependencies

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("dev.rlce.kloca:runtime:0.1.0")
            // Optional: Compose support
            implementation("dev.rlce.kloca:runtime-compose:0.1.0")
        }
    }
}
```

### 3. Configure (Optional)

```kotlin
kloca {
    defaultLanguage = "en"        // Language for Android values/ folder
    namespacePrefix = "MyApp"     // Prefix for StringKeys class
}
```

### 4. Create Translation Files

Place YAML files in `src/main/kloca/`:

**i18n.en.yaml:**
```yaml
i18n:
  greeting:
    hello: "Hello"
    welcome: "Welcome, %s!"
  navigation:
    home: "Home"
    settings: "Settings"
```

**i18n.es.yaml:**
```yaml
i18n:
  greeting:
    hello: "Hola"
    welcome: "¡Bienvenido, %s!"
  navigation:
    home: "Inicio"
    settings: "Configuración"
```

### 5. Generate Resources

```bash
./gradlew generateTranslations
```

## Generated Files

The plugin generates:

### Android Resources
```
src/androidMain/res/
├── values/strings.xml           # Default language
├── values-es/strings.xml        # Spanish
└── values-fr/strings.xml        # French
```

### iOS Resources
```
src/iosMain/resources/
├── en.lproj/Localizable.strings
├── es.lproj/Localizable.strings
└── fr.lproj/Localizable.strings
```

### Type-safe Constants
```kotlin
// build/generated/kloca/StringKeys.kt
object StringKeys {
    const val GREETING_HELLO = "greeting.hello"
    const val GREETING_WELCOME = "greeting.welcome"
    const val NAVIGATION_HOME = "navigation.home"
    const val NAVIGATION_SETTINGS = "navigation.settings"
}
```

## Configuration Options

### Default Language

Controls which language goes into Android's default `values/` folder:

```kotlin
kloca {
    defaultLanguage = "es"  // Spanish becomes default
}
```

**Result:**
- `values/strings.xml` → Spanish content
- `values-en/strings.xml` → English content

### Namespace Prefix

Controls the generated StringKeys class name:

```kotlin
kloca {
    namespacePrefix = "Auth"  // Generates AuthStringKeys
}
```

## Gradle Tasks

### generateTranslations
Processes YAML files and generates all resources and code.

**Automatic Dependencies:**
- Runs before `compileKotlin*` tasks
- Runs before Android resource merging tasks
- Runs before iOS Compose resource tasks

### Manual Execution
```bash
# Generate translations only
./gradlew generateTranslations

# Clean and regenerate
./gradlew clean generateTranslations

# Build with fresh translations
./gradlew clean build
```

## Multi-Module Support

Apply the plugin to each module that needs translations:

```kotlin
// Module 1: feature-auth/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca")
}

kloca {
    namespacePrefix = "Auth"  // → AuthStringKeys
}

// Module 2: feature-profile/build.gradle.kts
plugins {
    kotlin("multiplatform")
    id("dev.rlce.kloca")
}

kloca {
    namespacePrefix = "Profile"  // → ProfileStringKeys
}
```

Each module gets its own `StringKeys` class and resource files.

## YAML File Format

### Basic Structure
```yaml
i18n:
  key: "Value"
  nested:
    key: "Nested value"
```

### Supported Features

- **Nested Keys**: `feature.screen.title` becomes `FEATURE_SCREEN_TITLE`
- **String Interpolation**: Use `%s`, `%d` for parameters
- **Multiple Languages**: One file per language (`i18n.en.yaml`, `i18n.es.yaml`)

### File Naming Conventions

- `i18n.en.yaml` → English (language: `en`)
- `i18n.es-ES.yaml` → Spanish Spain (language: `es-ES`)
- `en.yaml` → English (fallback)
- `i18n.yaml` → Default language (`en`)

## Troubleshooting

### Common Issues

**"No YAML files found"**
- Ensure files are in `src/main/kloca/`
- Check file extensions (`.yaml` or `.yml`)

**"YAML parsing failed"**
- Validate YAML syntax
- Ensure proper indentation (spaces, not tabs)
- Check for missing `i18n:` root key

**"Build fails after plugin application"**
- Run `./gradlew clean generateTranslations`
- Check that runtime dependencies are added
- Verify Gradle and KSP versions

### Debug Generated Files

```bash
# View generated resources
ls -la build/generated/kloca/

# Check Android resources
ls -la src/androidMain/res/values*/

# Check iOS resources
ls -la src/iosMain/resources/*.lproj/
```

## Requirements

- Gradle 8.0+
- Kotlin 2.2.0+
- KSP 2.2.0-2.0.2+
- Android Gradle Plugin 8.0+ (for Android targets)

## Plugin Architecture

### Key Components

- **KlocaPlugin**: Main plugin entry point and Gradle integration
- **GenerateTranslationsTask**: Gradle task that orchestrates generation
- **YamlProcessor**: Handles YAML parsing and validation
- **ResourceGenerator**: Creates platform-specific resource files
- **KlocaExtension**: Configuration DSL

### Gradle Integration

The plugin automatically integrates with:
- KSP for code generation
- Android resource processing
- Kotlin compilation pipeline
- Source set management

This ensures translations are always generated before compilation and resources are properly included in builds.