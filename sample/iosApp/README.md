# KlocaSample iOS App

This iOS app demonstrates the usage of the Kloca i18n plugin with iOS integration.

## Setup

1. **Build the Kotlin Multiplatform Framework**:
   ```bash
   cd ../..
   ./gradlew :sample:embedAndSignAppleFrameworkForXcode
   ```

2. **Open the Xcode Project**:
   ```bash
   open KlocaSample.xcodeproj
   ```

3. **Run the App**:
   - Select a simulator or device
   - Press Cmd+R to build and run

## Features

- **Shared UI**: Uses the same Compose UI as Android through `SampleApp()`
- **Localization**: Supports English and Spanish through iOS `.strings` files
- **StringProvider**: Uses iOS `NSLocalizedString` for string retrieval
- **Type-safe Keys**: Uses generated `SampleStringKeys` for compile-time safety

## Localization Files

- `en.lproj/Localizable.strings` - English translations
- `es.lproj/Localizable.strings` - Spanish translations

## Architecture

- `KlocaSampleApp.swift` - App entry point
- `ContentView.swift` - SwiftUI wrapper for Compose UI
- `MainViewController()` - Kotlin function that returns the Compose UI controller

## Troubleshooting

If the framework is not found:
1. Make sure you've built the KMP framework first
2. Check that `FRAMEWORK_SEARCH_PATHS` in Xcode build settings includes the correct path
3. Verify the framework exists in `../build/xcode-frameworks/`