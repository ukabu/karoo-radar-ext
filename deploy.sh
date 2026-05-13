#!/bin/bash
set -euo pipefail

APP_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$APP_DIR"

echo "=== Karoo Radar Extension Build & Deploy ==="

# Build debug APK
./gradlew assembleDebug

# Find the APK
APK=$(find app/build/outputs/apk/debug -name "*.apk" | head -1)
if [ -z "$APK" ]; then
    echo "ERROR: No APK found after build"
    exit 1
fi

echo "APK: $APK"

# Check for connected Karoo
if ! adb devices | grep -q "device$"; then
    echo "WARNING: No Android device connected via ADB"
    echo "Connect your Karoo 3 via USB and try again"
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
