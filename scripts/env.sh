#!/usr/bin/env bash
# Source this to get Android SDK tools on your PATH for local development.
#   source scripts/env.sh
#
# Auto-detects the Android SDK, exports ANDROID_HOME and puts platform-tools
# (adb) on PATH. Safe to source repeatedly.

# --- Android SDK ---
if [ -z "${ANDROID_HOME:-}" ] && [ -z "${ANDROID_SDK_ROOT:-}" ]; then
    for candidate in \
        "$HOME/android-sdk" \
        "$HOME/Android/Sdk" \
        "$HOME/Library/Android/sdk" \
        "/usr/local/android-sdk"
    do
        if [ -d "$candidate/platforms" ]; then
            export ANDROID_HOME="$candidate"
            break
        fi
    done
fi

export ANDROID_HOME="${ANDROID_HOME:-${ANDROID_SDK_ROOT:-}}"

if [ -n "${ANDROID_HOME:-}" ] && [ -d "$ANDROID_HOME/platform-tools" ]; then
    case ":$PATH:" in
        *":$ANDROID_HOME/platform-tools:"*) ;;
        *) export PATH="$ANDROID_HOME/platform-tools:$PATH" ;;
    esac
fi

# --- JDK 17 (only if JAVA_HOME is unset) ---
if [ -z "${JAVA_HOME:-}" ]; then
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
            break
        fi
    done
fi

if command -v adb >/dev/null 2>&1; then
    echo "env.sh: ANDROID_HOME=${ANDROID_HOME:-<unset>}  JAVA_HOME=${JAVA_HOME:-<unset>}  adb=$(command -v adb)"
else
    echo "env.sh: WARNING - adb not found; set ANDROID_HOME or install platform-tools" >&2
fi
