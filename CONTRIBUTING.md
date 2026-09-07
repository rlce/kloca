# Contributing to Kloca

Thank you for your interest in contributing to Kloca! We welcome contributions from the community and are grateful for any help you can provide.

## Table of Contents

- [Code of Conduct](#code-of-conduct)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Contributing Guidelines](#contributing-guidelines)
- [Pull Request Process](#pull-request-process)
- [Issue Guidelines](#issue-guidelines)
- [Code Style](#code-style)
- [Testing](#testing)
- [Documentation](#documentation)

## Code of Conduct

This project and everyone participating in it is governed by the [Kloca Code of Conduct](CODE_OF_CONDUCT.md). By participating, you are expected to uphold this code.

## Getting Started

### Development Setup

1. **Fork and Clone**
   ```bash
   git clone https://github.com/YOUR_USERNAME/kloca.git
   cd kloca
   ```

2. **Prerequisites**
   - JDK 17 or higher
   - Gradle 8.0+ (included wrapper)
   - Git

3. **Build the Project**
   ```bash
   ./gradlew build
   ```

4. **Run Tests**
   ```bash
   ./gradlew test
   ```

5. **Format Code (Required before committing)**
   ```bash
   sh ../scripts/format.sh
   ```

6. **Test with Sample Project**
   ```bash
   ./gradlew :sample-android-app:assembleDebug
   ./gradlew generateTranslations
   ```

### Project Structure

```
kloca/
├── kloca-gradle-plugin/    # Main Gradle plugin
├── kloca-runtime/         # Runtime library for string access
├── sample/               # Example usage project
├── .github/             # GitHub workflows and templates
└── docs/               # Documentation
```

## Contributing Guidelines

### Types of Contributions

We welcome several types of contributions:

- **Bug reports** - Help us identify and fix issues
- **Feature requests** - Suggest new functionality
- **Code contributions** - Bug fixes, features, improvements
- **Documentation** - Improve guides, API docs, examples
- **Testing** - Add test cases, improve coverage

### Before You Start

1. **Check existing issues** to avoid duplicate work
2. **Open an issue** for significant changes to discuss the approach
3. **Start small** with your first contribution

## Pull Request Process

### 1. Create a Feature Branch

```bash
git checkout -b feature/your-feature-name
# or
git checkout -b fix/your-bug-fix
```

### 2. Make Your Changes

- Follow the [code style](#code-style) guidelines
- Add tests for new functionality  
- Update documentation as needed
- **Run code formatting**: `sh ../scripts/format.sh`
- Ensure all tests pass

### 3. Commit Your Changes

Use conventional commit messages:

```bash
git commit -m "feat: add new YAML validation feature"
git commit -m "fix: resolve resource generation on Windows"
git commit -m "docs: improve setup instructions"
```

**Commit Types:**
- `feat:` - New features
- `fix:` - Bug fixes
- `docs:` - Documentation changes
- `test:` - Test additions or modifications
- `refactor:` - Code refactoring
- `perf:` - Performance improvements
- `ci:` - CI/CD changes

### 4. Push and Create Pull Request

```bash
git push origin feature/your-feature-name
```

Then create a pull request on GitHub with:
- Clear title and description
- Link to related issues
- Screenshots/examples if applicable
- Checklist of changes made

### 5. Pull Request Review

- All PRs require review before merging
- Address feedback promptly
- Keep PRs focused and reasonably sized
- Maintain a clean commit history

## Issue Guidelines

### Bug Reports

Please include:

- **Clear title** describing the issue
- **Steps to reproduce** the bug
- **Expected vs actual behavior**
- **Environment details** (OS, Kotlin version, Gradle version)
- **Sample code** or project if possible
- **Error messages** or stack traces

### Feature Requests

Please include:

- **Clear description** of the proposed feature
- **Use case** explaining why it would be valuable
- **Implementation ideas** if you have them
- **Alternatives considered**

### Issue Labels

We use labels to organize issues:

- `bug` - Something isn't working
- `enhancement` - New feature request
- `documentation` - Documentation improvements
- `good first issue` - Good for newcomers
- `help wanted` - Community assistance needed
- `priority: high/medium/low` - Issue priority

## Code Style

### Kotlin Style Guide

We follow the [official Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Use camelCase for functions and properties
- Use PascalCase for classes and objects
- Use UPPER_SNAKE_CASE for constants
- Prefer explicit type declarations for public APIs

### Code Formatting

We use [Spotless](https://github.com/diffplug/spotless) with ktlint for consistent code formatting. The project follows the official Kotlin code style with additional rules for code quality.

**Format your code before committing:**

```bash
# Format all code in the project
sh ../scripts/format.sh

# Alternative: Run Spotless directly (requires proper Gradle setup)
./gradlew --init-script buildscripts/init.gradle.kts spotlessApply
```

**Check formatting without applying changes:**

```bash
./gradlew --init-script buildscripts/init.gradle.kts spotlessCheck
```

**Formatting Rules:**
- No wildcard imports (use specific imports instead)
- Official Kotlin code style
- Maximum line length: 140 characters
- Trailing commas allowed for better diffs
- Automatic license header management (when configured)

### Documentation Comments

Use KDoc for public APIs:

```kotlin
/**
 * Processes YAML translation files and generates platform-specific resources.
 *
 * @param sourceDir Directory containing YAML translation files
 * @param outputDir Directory where generated resources will be written
 * @throws YamlProcessingException if YAML parsing fails
 */
fun processYamlFiles(sourceDir: File, outputDir: File) {
    // Implementation
}
```

## Testing

### Test Requirements

- **Unit tests** for all new functionality
- **Integration tests** for plugin behavior
- **Sample project** testing for end-to-end validation
- Maintain or improve test coverage

### Running Tests

```bash
# Run all tests
./gradlew test

# Run tests for specific module
./gradlew -p kloca-gradle-plugin test

# Run tests with coverage
./gradlew test jacocoTestReport
```

### Test Structure

```kotlin
class YamlProcessorTest {
    
    @Test
    fun `should parse valid YAML file correctly`() {
        // Given
        val yamlContent = """
            i18n:
              greeting:
                hello: "Hello"
        """.trimIndent()
        
        // When
        val result = yamlProcessor.parse(yamlContent)
        
        // Then
        assertEquals("Hello", result["greeting.hello"])
    }
}
```

## Documentation

### Types of Documentation

1. **API Documentation** - KDoc comments in code
2. **User Guides** - README and usage examples
3. **Developer Documentation** - Setup and contribution guides
4. **Changelog** - Track changes between versions

### Documentation Guidelines

- Write clear, concise explanations
- Include code examples for complex features
- Update documentation when changing functionality
- Use proper markdown formatting

## Release Process

### Version Numbering

We use [Semantic Versioning](https://semver.org/):
- `MAJOR.MINOR.PATCH` format
- Breaking changes increment MAJOR
- New features increment MINOR
- Bug fixes increment PATCH

### Release Checklist

1. Update version in `gradle.properties`
2. Update `CHANGELOG.md`
3. Create release PR
4. Tag release after merging
5. Publish to repositories
6. Update documentation

## Getting Help

- **GitHub Discussions** for questions and ideas
- **GitHub Issues** for bug reports and feature requests
- **Code reviews** for feedback on pull requests

## Recognition

Contributors are recognized in:
- `CHANGELOG.md` release notes
- GitHub contributors list
- Special mention for significant contributions

Thank you for contributing to Kloca! 🎉
