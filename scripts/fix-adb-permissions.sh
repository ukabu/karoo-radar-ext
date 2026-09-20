#!/usr/bin/env bash
# Fix ADB "no permissions" for the Hammerhead Karoo on Linux.
#
# The Karoo can enumerate with USB vendor 05c6 (Qualcomm) in addition to
# Google's 18d1, so the udev rule must match both. This script installs the
# rule, reloads udev, and restarts adb.
#
# Usage:
#   ./scripts/fix-adb-permissions.sh
#
# Requires root (uses sudo) and your user in the `plugdev` group.

set -euo pipefail

RULE_FILE="/etc/udev/rules.d/99-karoo.rules"

if [ "$(uname -s)" != "Linux" ]; then
    echo "This script is Linux-only (udev). On macOS, ADB permissions are not an issue."
    exit 0
fi

if [ "$(id -u)" -ne 0 ] && ! command -v sudo >/dev/null 2>&1; then
    echo "ERROR: need root or sudo to write $RULE_FILE" >&2
    exit 1
fi

if ! id -nG "$USER" | grep -qw plugdev; then
    echo "WARNING: user '$USER' is not in the 'plugdev' group."
    echo "  Run:  sudo usermod -aG plugdev $USER"
    echo "  then log out and back in."
    echo ""
fi

echo "Writing $RULE_FILE ..."
sudo tee "$RULE_FILE" >/dev/null <<'EOF'
# Hammerhead Karoo (Google 18d1 and Qualcomm 05c6 USB IDs)
SUBSYSTEM=="usb", ATTR{idVendor}=="18d1", MODE="0666", GROUP="plugdev"
SUBSYSTEM=="usb", ATTR{idVendor}=="05c6", ATTR{idProduct}=="901d", MODE="0666", GROUP="plugdev"
EOF

echo "Reloading udev rules ..."
sudo udevadm control --reload-rules
sudo udevadm trigger

echo "Restarting adb ..."
if ! command -v adb >/dev/null 2>&1; then
    # Fall back to a locally installed platform-tools.
    if [ -n "${ANDROID_HOME:-}" ] && [ -x "$ANDROID_HOME/platform-tools/adb" ]; then
        export PATH="$ANDROID_HOME/platform-tools:$PATH"
    elif [ -x "$HOME/android-sdk/platform-tools/adb" ]; then
        export PATH="$HOME/android-sdk/platform-tools:$PATH"
    else
        echo "WARNING: adb not found; skipping adb restart. Run it manually after replugging the Karoo." >&2
    fi
fi

if command -v adb >/dev/null 2>&1; then
    adb kill-server >/dev/null 2>&1 || true
    adb start-server >/dev/null 2>&1 || true

    echo ""
    echo "Current devices:"
    adb devices -l

    if adb devices | grep -q "no permissions"; then
        echo ""
        echo "Still 'no permissions'. Unplug and replug the Karoo, then re-run: adb devices -l"
    fi
fi

echo ""
echo "Done. If the device is not listed yet, unplug/replug the Karoo once."
