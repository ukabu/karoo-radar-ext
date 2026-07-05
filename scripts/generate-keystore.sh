#!/bin/bash
set -euo pipefail

# Generate a fresh Android release keystore for the Karoo Radar Extension.
# The keystore file is NOT committed; its base64 encoding and passwords are
# stored in GitHub Secrets for CI signing.

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"
KEYSTORE_PATH="$PROJECT_DIR/karoo-radar-release.keystore"
ALIAS="karoo-radar"
VALIDITY_DAYS=9125

if [ -f "$KEYSTORE_PATH" ]; then
    echo "Keystore already exists at $KEYSTORE_PATH"
    echo "Delete it first if you want to regenerate."
    exit 1
fi

# Generate strong random passwords. PKCS12 uses the same password for store and key.
PASSWORD="$(openssl rand -base64 48 | tr -dc 'a-zA-Z0-9' | head -c 32)"

export JAVA_HOME="${JAVA_HOME:-/home/ukabu/tools/jdk-17.0.11+9}"
if [ ! -x "$JAVA_HOME/bin/keytool" ]; then
    echo "ERROR: keytool not found at $JAVA_HOME/bin/keytool"
    echo "Set JAVA_HOME to a JDK 17+ installation."
    exit 1
fi

"$JAVA_HOME/bin/keytool" -genkeypair \
    -v \
    -keystore "$KEYSTORE_PATH" \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity "$VALIDITY_DAYS" \
    -storetype PKCS12 \
    -storepass "$PASSWORD" \
    -keypass "$PASSWORD" \
    -dname "CN=Karoo Radar Extension" \
    2>&1 | tail -5

B64="$(base64 -w 0 "$KEYSTORE_PATH")"

echo ""
echo "=== Keystore generated ==="
echo "Path: $KEYSTORE_PATH"
echo "Alias: $ALIAS"
echo "Store/Key password: $PASSWORD"
echo ""
echo "=== Add these GitHub Secrets ==="
echo "SIGNING_KEYSTORE_BASE64=$B64"
echo "SIGNING_KEYSTORE_PASSWORD=$PASSWORD"
echo "SIGNING_KEY_ALIAS=$ALIAS"
echo "SIGNING_KEY_PASSWORD=$PASSWORD"
echo ""
echo "=== Back up the keystore securely ==="
echo "This file is required to publish updates. Store it in a password manager"
echo "or secure backup. If lost, users will need to reinstall the extension."
