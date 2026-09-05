#!/bin/bash

# Publishing script for Kloca project
# This script provides convenient commands for publishing to different repositories

set -e

# Load credentials from external script if it exists
if [[ -f "kloca_credentials.sh" ]]; then
    echo "Loading credentials from kloca_credentials.sh..."
    source kloca_credentials.sh
elif [[ -f "./kloca_credentials.sh" ]]; then
    echo "Loading credentials from ./kloca_credentials.sh..."
    source ./kloca_credentials.sh
else
    echo "No kloca_credentials.sh found, using existing environment variables or properties files..."
fi

show_help() {
    echo "Usage: $0 [option]"
    echo ""
    echo "Options:"
    echo "  --local         Build and publish to local Maven repository"
    echo "  --plugin        Publish Gradle plugin to Maven Central (requires credentials)"
    echo "  --runtime       Publish runtime modules to Maven Central (requires credentials)"
    echo "  --check         Check publishing environment setup"
    echo "  --help, -h      Show this help message"
    echo ""
    echo "Environment variables for Maven Central publishing:"
    echo "  ORG_GRADLE_PROJECT_mavenCentralUsername - Maven Central username"
    echo "  ORG_GRADLE_PROJECT_mavenCentralPassword - Maven Central password"
    echo "  ORG_GRADLE_PROJECT_signingInMemoryKey   - Base64 encoded signing key"
    echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword - Signing key password"
    echo ""
    echo "Environment variables for Gradle Plugin Portal:"
    echo "  GRADLE_PUBLISH_KEY    - Gradle Plugin Portal key"
    echo "  GRADLE_PUBLISH_SECRET - Gradle Plugin Portal secret"
}

check_environment() {
    echo "Checking publishing environment..."

    echo "✓ Java version:"
    java -version

    echo ""
    echo "✓ Gradle version:"
    ./gradlew --version

    echo ""
    echo "Maven Central publishing environment:"
    # Check username
    if [[ -n "${ORG_GRADLE_PROJECT_mavenCentralUsername:-}" ]] || \
       [[ -n "$(grep -E '^mavenCentralUsername=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^mavenCentralUsername=' local.properties 2>/dev/null || true)" ]]; then
        echo "  ✓ Maven Central username is set"
    else
        echo "  ✗ Maven Central username not set"
    fi

    # Check password
    if [[ -n "${ORG_GRADLE_PROJECT_mavenCentralPassword:-}" ]] || \
       [[ -n "$(grep -E '^mavenCentralPassword=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^mavenCentralPassword=' local.properties 2>/dev/null || true)" ]]; then
        echo "  ✓ Maven Central password is set"
    else
        echo "  ✗ Maven Central password not set"
    fi

    # Check signing configuration (either in-memory or file-based)
    local has_in_memory_signing=false
    local has_file_based_signing=false

    # Check in-memory signing
    if [[ -n "${ORG_GRADLE_PROJECT_signingInMemoryKey:-}" ]] || \
       [[ -n "$(grep -E '^signingInMemoryKey=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^signingInMemoryKey=' local.properties 2>/dev/null || true)" ]]; then
        if [[ -n "${ORG_GRADLE_PROJECT_signingInMemoryKeyPassword:-}" ]] || \
           [[ -n "$(grep -E '^signingInMemoryKeyPassword=' gradle.properties 2>/dev/null || true)" ]] || \
           [[ -n "$(grep -E '^signingInMemoryKeyPassword=' local.properties 2>/dev/null || true)" ]]; then
            has_in_memory_signing=true
        fi
    fi

    # Check file-based signing
    if [[ -n "$(grep -E '^signing.keyId=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^signing.keyId=' local.properties 2>/dev/null || true)" ]]; then
        if [[ -n "$(grep -E '^signing.secretKeyRingFile=' gradle.properties 2>/dev/null || true)" ]] || \
           [[ -n "$(grep -E '^signing.secretKeyRingFile=' local.properties 2>/dev/null || true)" ]]; then
            has_file_based_signing=true
        fi
    fi

    if [[ "$has_in_memory_signing" == true ]] || [[ "$has_file_based_signing" == true ]]; then
        if [[ "$has_in_memory_signing" == true ]]; then
            echo "  ✓ In-memory signing is configured"
        fi
        if [[ "$has_file_based_signing" == true ]]; then
            echo "  ✓ File-based signing is configured"
        fi
    else
        echo "  ✗ Signing not configured (need either in-memory or file-based signing)"
    fi

    echo ""
    echo "Gradle Plugin Portal environment:"
    # Check plugin portal key
    if [[ -n "${GRADLE_PUBLISH_KEY:-}" ]] || \
       [[ -n "$(grep -E '^gradle.publish.key=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^gradle.publish.key=' local.properties 2>/dev/null || true)" ]]; then
        echo "  ✓ Plugin Portal key is set"
    else
        echo "  ✗ Plugin Portal key not set"
    fi

    # Check plugin portal secret
    if [[ -n "${GRADLE_PUBLISH_SECRET:-}" ]] || \
       [[ -n "$(grep -E '^gradle.publish.secret=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^gradle.publish.secret=' local.properties 2>/dev/null || true)" ]]; then
        echo "  ✓ Plugin Portal secret is set"
    else
        echo "  ✗ Plugin Portal secret not set"
    fi
}

publish_local() {
    echo "Publishing to local Maven repository..."
    ./gradlew publishToMavenLocal
    echo ""
    echo "✓ All modules published to local Maven repository"
    echo "You can find the artifacts in:"
    echo "  ~/.m2/repository/io/github/rlce/"
}

publish_plugin() {
    echo "Publishing Gradle plugin to Maven Central..."

    # Check if Maven Central credentials are configured
    local has_username=false
    local has_password=false

    if [[ -n "${ORG_GRADLE_PROJECT_mavenCentralUsername:-}" ]] || \
       [[ -n "$(grep -E '^mavenCentralUsername=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^mavenCentralUsername=' local.properties 2>/dev/null || true)" ]]; then
        has_username=true
    fi

    if [[ -n "${ORG_GRADLE_PROJECT_mavenCentralPassword:-}" ]] || \
       [[ -n "$(grep -E '^mavenCentralPassword=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^mavenCentralPassword=' local.properties 2>/dev/null || true)" ]]; then
        has_password=true
    fi

    if [[ "$has_username" == false ]] || [[ "$has_password" == false ]]; then
        echo "❌ Error: Maven Central credentials not configured"
        echo ""
        echo "Please set the following environment variables or add them to gradle.properties/local.properties:"
        echo "  ORG_GRADLE_PROJECT_mavenCentralUsername (or mavenCentralUsername in properties files)"
        echo "  ORG_GRADLE_PROJECT_mavenCentralPassword (or mavenCentralPassword in properties files)"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKey (or signingInMemoryKey in properties files)"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword (or signingInMemoryKeyPassword in properties files)"
        echo ""
        echo "Note: local.properties is recommended for local development (automatically gitignored)"
        exit 1
    fi

    echo "Publishing kloca-gradle-plugin..."
    ./gradlew -p kloca-gradle-plugin publishToMavenCentral --no-configuration-cache
    echo ""
    echo "✓ Gradle plugin published to Maven Central (staging)"
}

publish_runtime() {
    echo "Publishing runtime modules to Maven Central..."

    # Check if Maven Central credentials are configured
    local has_username=false
    local has_password=false

    if [[ -n "${ORG_GRADLE_PROJECT_mavenCentralUsername:-}" ]] || \
       [[ -n "$(grep -E '^mavenCentralUsername=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^mavenCentralUsername=' local.properties 2>/dev/null || true)" ]]; then
        has_username=true
    fi

    if [[ -n "${ORG_GRADLE_PROJECT_mavenCentralPassword:-}" ]] || \
       [[ -n "$(grep -E '^mavenCentralPassword=' gradle.properties 2>/dev/null || true)" ]] || \
       [[ -n "$(grep -E '^mavenCentralPassword=' local.properties 2>/dev/null || true)" ]]; then
        has_password=true
    fi

    if [[ "$has_username" == false ]] || [[ "$has_password" == false ]]; then
        echo "❌ Error: Maven Central credentials not configured"
        echo ""
        echo "Please set the following environment variables or add them to gradle.properties/local.properties:"
        echo "  ORG_GRADLE_PROJECT_mavenCentralUsername (or mavenCentralUsername in properties files)"
        echo "  ORG_GRADLE_PROJECT_mavenCentralPassword (or mavenCentralPassword in properties files)"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKey (or signingInMemoryKey in properties files)"
        echo "  ORG_GRADLE_PROJECT_signingInMemoryKeyPassword (or signingInMemoryKeyPassword in properties files)"
        echo ""
        echo "Note: local.properties is recommended for local development (automatically gitignored)"
        exit 1
    fi

    # Publish runtime modules using Vanniktech Maven Publish plugin
    # This will publish to Maven Central staging repository (manual release required)
    echo "Publishing kloca-runtime..."
    ./gradlew :kloca-runtime:publishToMavenCentral --no-configuration-cache

    echo "Publishing kloca-runtime-compose..."
    ./gradlew :kloca-runtime-compose:publishToMavenCentral --no-configuration-cache

    echo ""
    echo "✓ Runtime modules published to Maven Central (staging)"
}

# Main script logic
case "${1:-}" in
    --local)
        publish_local
        ;;
    --plugin)
        publish_plugin
        ;;
    --runtime)
        publish_runtime
        ;;
    --check)
        check_environment
        ;;
    --help|-h)
        show_help
        ;;
    "")
        echo "Error: No option provided"
        echo ""
        show_help
        exit 1
        ;;
    *)
        echo "Error: Unknown option '$1'"
        echo ""
        show_help
        exit 1
        ;;
esac
