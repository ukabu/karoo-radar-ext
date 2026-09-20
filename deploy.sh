#!/bin/bash
set -euo pipefail

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$APP_DIR"

echo "=== Karoo Radar Extension Build & Deploy ==="

# --- Check Java ---
if [ -z "${JAVA_HOME:-}" ]; then
    # Try common locations
    for candidate in \
        "$HOME/tools/jdk-17.0.11+9" \
        "/usr/lib/jvm/java-17-openjdk" \
        "/usr/lib/jvm/java-17-openjdk-amd64" \
        "/usr/local/opt/openjdk@17" \
        "/opt/homebrew/opt/openjdk@17" \
        "/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
    do
        if [ -d "$candidate" ] && [ -x "$candidate/bin/java" ]; then
            export JAVA_HOME="$candidate"
            echo "Auto-detected JAVA_HOME=$JAVA_HOME"
            break
        fi
    done
fi

if [ -z "${JAVA_HOME:-}" ] && command -v java >/dev/null 2>&1; then
    # Try to resolve from `java` command
    JAVA_BIN=$(command -v java)
    if [ -L "$JAVA_BIN" ]; then
        JAVA_BIN=$(readlink -f "$JAVA_BIN" 2>/dev/null || readlink "$JAVA_BIN")
    fi
    # Walk up from bin/java to the JDK root
    if [[ "$JAVA_BIN" == */bin/java ]]; then
        export JAVA_HOME="${JAVA_BIN%/bin/java}"
        echo "Resolved JAVA_HOME from PATH: $JAVA_HOME"
    fi
fi

if [ -z "${JAVA_HOME:-}" ] || [ ! -x "$JAVA_HOME/bin/java" ]; then
    echo "ERROR: JAVA_HOME is not set and Java 17 could not be auto-detected."
    echo ""
    echo "To fix:"
    echo "  1. Install Temurin JDK 17 (or any JDK 17+)"
    echo "     Linux:  curl -sL https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.11%2B9/OpenJDK17U-jdk_x64_linux_hotspot_17.0.11_9.tar.gz | tar -xz"
    echo "     macOS:  brew install --cask temurin17"
    echo ""
    echo "  2. Then set JAVA_HOME before running deploy.sh:"
    echo "     export JAVA_HOME=/path/to/your/jdk-17"
    echo "     ./deploy.sh"
    exit 1
fi

# Verify Java version
JAVA_VER=$($JAVA_HOME/bin/java -version 2>&1 | head -1 | grep -o '"[0-9.]*"' | tr -d '"')
echo "Using Java $JAVA_VER from $JAVA_HOME"

# --- Check Android SDK ---
if [ -z "${ANDROID_HOME:-}" ] && [ -z "${ANDROID_SDK_ROOT:-}" ]; then
    for candidate in \
        "$HOME/android-sdk" \
        "$HOME/Android/Sdk" \
        "$HOME/Library/Android/sdk" \
        "/usr/local/android-sdk"
    do
        if [ -d "$candidate/platforms" ]; then
            export ANDROID_HOME="$candidate"
            echo "Auto-detected ANDROID_HOME=$ANDROID_HOME"
            break
        fi
    done
fi

if [ -z "${ANDROID_HOME:-}" ] || [ ! -d "${ANDROID_HOME}/platforms" ]; then
    echo "ERROR: ANDROID_HOME not set and Android SDK could not be auto-detected."
    echo ""
    echo "To fix:"
    echo "  1. Download Android command-line tools:"
    echo "     https://developer.android.com/studio#command-tools"
    echo "  2. Install platform 35 + build-tools:"
    echo "     sdkmanager \"platforms;android-35\" \"build-tools;35.0.0\" \"platform-tools\""
    echo "  3. Then set ANDROID_HOME:"
    echo "     export ANDROID_HOME=/path/to/android-sdk"
    echo "     ./deploy.sh"
    exit 1
fi

# --- Build debug APK ---
echo ""
echo "Building debug APK..."
./gradlew assembleDebug

# Find the APK
APK=$(find app/build/outputs/apk/debug -name "*.apk" | head -1)
if [ -z "$APK" ]; then
    echo "ERROR: No APK found after build"
    exit 1
fi

echo "APK: $APK"

# --- Check for connected Karoo ---
echo ""
if ! command -v adb >/dev/null 2>&1; then
    if [ -x "$ANDROID_HOME/platform-tools/adb" ]; then
        export PATH="$ANDROID_HOME/platform-tools:$PATH"
    else
        echo "ERROR: adb not found. Is platform-tools installed?"
        exit 1
    fi
fi

if ! adb devices | grep -q "device$"; then
    if adb devices | grep -q "no permissions"; then
        echo "ERROR: Karoo detected but ADB has no permission to access it."
        echo ""
        echo "This is a Linux udev rule mismatch. The Karoo currently enumerates with"
        echo "USB vendor 05c6 (Qualcomm), but the udev rule only matches 18d1 (Google)."
        echo ""
        echo "Fix it automatically with:"
        echo "  ./scripts/fix-adb-permissions.sh"
        echo ""
        echo "Then unplug/replug the Karoo once and re-run this script."
    else
        echo "WARNING: No Android device connected via ADB"
        echo "Connect your Karoo 3 via USB and try again"
    fi
    exit 1
fi

# Install
echo "Installing to Karoo..."
adb install -r "$APK"

echo ""
echo "=== Install Complete ==="
echo "Next steps:"
echo "1. On Karoo, go to Settings > Extensions"
echo "2. Enable 'Karoo Radar'"
echo "3. Add datafields to your ride profile"
echo "4. Start a ride and pair your ANT+ radar"
