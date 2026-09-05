# Kloca Runtime Compose

Jetpack Compose integration for Kloca that provides reactive localization functions and automatic recomposition when language changes.

## Overview

This module extends Kloca runtime with Compose-specific functions that automatically handle:
- **Reactive Updates**: Recompose when language changes
- **Performance Optimization**: Efficient flow-based state management
- **Type Safety**: Extension functions for StringKeys
- **Ease of Use**: Simple `@Composable` functions

## Quick Start

### 1. Add Dependencies

```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.rlce:kloca-runtime:0.2.0")
            implementation("io.github.rlce:kloca-runtime-compose:0.2.0")
            implementation(compose.runtime)
        }
    }
}
```

### 2. Initialize Kloca

```kotlin
// Initialize Kloca as usual
Kloca.initialize(context, arrayOf("en", "es", "fr"))
```

### 3. Use in Compose

```kotlin
@Composable
fun MyScreen() {
    Column {
        // Direct string access
        Text(localizedString("greeting.hello"))

        // With parameters
        Text(localizedString("greeting.welcome", "John"))

        // Type-safe access
        Text(StringKeys.GREETING_HELLO.localized())
        Text(StringKeys.GREETING_WELCOME.localized("John"))
    }
}
```

## API Reference

### Core Functions

#### localizedString
Get localized strings with automatic recomposition:

```kotlin
@Composable
fun localizedString(key: String): String

@Composable
fun localizedString(key: String, vararg args: Any): String
```

**Example:**
```kotlin
@Composable
fun WelcomeMessage(userName: String) {
    Text(
        text = localizedString("welcome.message", userName),
        style = MaterialTheme.typography.headlineSmall
    )
}
```

#### Extension Functions

Type-safe alternatives using extension functions:

```kotlin
@Composable
fun String.localized(): String

@Composable
fun String.localized(vararg args: Any): String
```

**Example:**
```kotlin
@Composable
fun NavigationBar() {
    BottomNavigation {
        BottomNavigationItem(
            label = { Text(StringKeys.NAV_HOME.localized()) },
            onClick = { /* navigate */ }
        )
        BottomNavigationItem(
            label = { Text(StringKeys.NAV_SETTINGS.localized()) },
            onClick = { /* navigate */ }
        )
    }
}
```

### Reactive Hooks

#### useLanguageChange
Get current language with automatic updates:

```kotlin
@Composable
fun useLanguageChange(): String
```

**Example:**
```kotlin
@Composable
fun LanguageDisplay() {
    val currentLanguage = useLanguageChange()

    Text("Language: ${currentLanguage.uppercase()}")
}
```

#### useLanguageModeChange
Get current language mode (SYSTEM vs USER_PREFERENCE):

```kotlin
@Composable
fun useLanguageModeChange(): Kloca.LanguageMode
```

**Example:**
```kotlin
@Composable
fun LanguageModeIndicator() {
    val mode = useLanguageModeChange()

    Icon(
        imageVector = when (mode) {
            Kloca.LanguageMode.SYSTEM -> Icons.Default.Language
            Kloca.LanguageMode.USER_PREFERENCE -> Icons.Default.Person
        },
        contentDescription = localizedString("language.mode")
    )
}
```

## Usage Patterns

### Basic Localization

```kotlin
@Composable
fun ProductCard(product: Product) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.headlineSmall
            )
            Text(
                text = localizedString("product.price", product.price),
                style = MaterialTheme.typography.bodyMedium
            )
            Button(
                onClick = { /* add to cart */ }
            ) {
                Text(StringKeys.PRODUCT_ADD_TO_CART.localized())
            }
        }
    }
}
```

### Language Selection Screen

```kotlin
@Composable
fun LanguageSelectionScreen() {
    val currentLanguage = useLanguageChange()
    val availableLanguages = remember { Kloca.getAvailableLanguages() }

    LazyColumn {
        items(availableLanguages) { languageCode ->
            LanguageItem(
                languageCode = languageCode,
                isSelected = languageCode == currentLanguage,
                onSelect = { Kloca.setUserLanguage(languageCode) }
            )
        }
    }
}

@Composable
fun LanguageItem(
    languageCode: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val displayName = languageCode.uppercase()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onSelect
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = displayName,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
```

### Dynamic Content Updates

```kotlin
@Composable
fun DynamicContentScreen() {
    val currentLanguage = useLanguageChange()

    // Content that changes based on language
    val greeting = remember(currentLanguage) {
        when (currentLanguage) {
            "es" -> "¡Hola!"
            "fr" -> "Salut!"
            else -> "Hello!"
        }
    }

    Column {
        Text(
            text = greeting,
            style = MaterialTheme.typography.displayLarge
        )

        Text(
            text = localizedString("app.description"),
            style = MaterialTheme.typography.bodyLarge
        )

        // This will automatically update when language changes
        Button(
            onClick = { /* action */ }
        ) {
            Text(StringKeys.COMMON_CONTINUE.localized())
        }
    }
}
```

### Settings Screen with Language Toggle

```kotlin
@Composable
fun SettingsScreen() {
    val languageMode = useLanguageModeChange()
    val currentLanguage = useLanguageChange()

    Column {
        Text(
            text = StringKeys.SETTINGS_TITLE.localized(),
            style = MaterialTheme.typography.headlineMedium
        )

        // Language mode toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(StringKeys.SETTINGS_USE_SYSTEM_LANGUAGE.localized())
            Switch(
                checked = languageMode == Kloca.LanguageMode.SYSTEM,
                onCheckedChange = { useSystem ->
                    if (useSystem) {
                        Kloca.useSystemLanguage()
                    } else {
                        // Show language selection
                    }
                }
            )
        }

        // Current language display
        Text(
            text = localizedString(
                "settings.current_language",
                currentLanguage.uppercase()
            ),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
```

## Performance Optimization

### Automatic Optimization
The Compose integration automatically optimizes performance through:

1. **Flow-based State**: Uses `collectAsState()` for efficient updates
2. **Smart Recomposition**: Only recomposes when language actually changes
3. **Minimal State**: Combines language mode and current language into single state
4. **Remember Keys**: Properly keys `remember()` blocks for cache efficiency

### Manual Optimization
For expensive operations, use manual optimization:

```kotlin
@Composable
fun ExpensiveLocalizedContent() {
    val currentLanguage = useLanguageChange()

    // Expensive computation only runs when language changes
    val processedContent = remember(currentLanguage) {
        expensiveProcessing(currentLanguage)
    }

    Text(processedContent)
}
```

## Advanced Usage

### Custom Localization Hook

Create custom hooks for specific use cases:

```kotlin
@Composable
fun useLocalizedList(keys: List<String>): List<String> {
    val currentLanguage = useLanguageChange()

    return remember(keys, currentLanguage) {
        keys.map { key -> Kloca.getString(key) }
    }
}

// Usage
@Composable
fun CategoryList() {
    val categoryKeys = listOf("cat.electronics", "cat.clothing", "cat.books")
    val categories = useLocalizedList(categoryKeys)

    LazyColumn {
        items(categories) { category ->
            Text(category)
        }
    }
}
```

### Formatting with Rich Text

```kotlin
@Composable
fun FormattedText() {
    val text = localizedString("terms.agreement", "Privacy Policy", "Terms of Service")

    // For complex formatting, consider using AnnotatedString
    val annotatedText = remember(text) {
        buildAnnotatedString {
            // Parse and style the formatted text
            append(text)
        }
    }

    Text(annotatedText)
}
```

### Testing Support

```kotlin
@Composable
fun TestableLocalizedComponent() {
    if (LocalInspectionMode.current) {
        // Show placeholder text during preview/testing
        Text("Localized content placeholder")
    } else {
        Text(localizedString("actual.key"))
    }
}
```

## Integration with Material Design

### Navigation
```kotlin
@Composable
fun AppNavigation() {
    NavigationBar {
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Home, StringKeys.NAV_HOME.localized()) },
            label = { Text(StringKeys.NAV_HOME.localized()) }
        )
    }
}
```

### Dialogs
```kotlin
@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(StringKeys.DIALOG_CONFIRM_TITLE.localized()) },
        text = { Text(StringKeys.DIALOG_CONFIRM_MESSAGE.localized()) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(StringKeys.COMMON_YES.localized())
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(StringKeys.COMMON_NO.localized())
            }
        }
    )
}
```

## Migration from Static Strings

### Before (Static)
```kotlin
@Composable
fun OldComponent() {
    Text("Hello World")
    Button(onClick = {}) {
        Text("Click Me")
    }
}
```

### After (Localized)
```kotlin
@Composable
fun NewComponent() {
    Text(StringKeys.GREETING_HELLO.localized())
    Button(onClick = {}) {
        Text(StringKeys.BUTTON_CLICK_ME.localized())
    }
}
```

## Requirements

- Kotlin 2.2.0+
- Compose Runtime (included with Compose BOM)

## Best Practices

1. **Use Type-safe Keys**: Prefer `StringKeys.KEY.localized()` over `localizedString("key")`
2. **Optimize Performance**: Use `remember()` for expensive computations
3. **Test Thoroughly**: Test language changes in different screens
4. **Handle Edge Cases**: Provide fallbacks for missing translations
5. **Use Semantic Keys**: Make translation keys descriptive and organized

## Troubleshooting

### Common Issues

**"Recomposition not happening"**
- Ensure Kloca is properly initialized
- Check that you're using `localizedString()` or `.localized()`
- Verify language change is being triggered

**"Performance issues with many strings"**
- Use `remember()` for expensive operations
- Consider lazy loading for large lists
- Profile composition frequency

**"Previews not working"**
- Use `LocalInspectionMode.current` for preview-specific content
- Initialize Kloca in preview parameters
- Provide fallback strings for previews
