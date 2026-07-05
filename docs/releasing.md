# Releasing Karoo Radar Extension

This document describes how to create and publish official releases of the Karoo Radar Extension.

## Release Pipeline Overview

1. Conventional commits land on `main`.
2. `release-please` opens (or updates) a Release PR.
3. A maintainer reviews and merges the Release PR.
4. `release-please` creates a git tag and GitHub Release with release notes.
5. A GitHub Actions workflow builds and signs the release APK and attaches it to the GitHub Release.
6. A maintainer downloads the signed APK from the GitHub Release and uploads it manually to the [Hammerhead Extension Library dashboard](https://dashboard.hammerhead.io).

## Keystore Setup (One-Time)

The release APK must be signed with the same keystore for every release. Losing the keystore means users will have to uninstall and reinstall the extension to receive updates.

### Generate the keystore

Run the helper script:

```bash
./scripts/generate-keystore.sh
```

This creates `karoo-radar-release.keystore` and prints:

- `SIGNING_KEYSTORE_BASE64`
- `SIGNING_KEYSTORE_PASSWORD`
- `SIGNING_KEY_ALIAS`
- `SIGNING_KEY_PASSWORD`

### Store secrets in GitHub

Add the four values printed by the script as repository secrets:

1. Go to **Settings > Secrets and variables > Actions** in the GitHub repository.
2. Click **New repository secret**.
3. Add each secret name exactly as shown above.

### Back up the keystore securely

- Store `karoo-radar-release.keystore` in a password manager or encrypted backup.
- Store the password in the same password manager.
- Do not commit the keystore or password to the repository (they are already ignored by `.gitignore`).

## Creating a Release

1. Ensure all changes on `main` use [Conventional Commits](https://www.conventionalcommits.org/).
2. Wait for `release-please` to create a Release PR.
3. Review the Release PR; it updates `versionName`, `versionCode`, `CHANGELOG.md`, and `.release-please-manifest.json`.
4. Merge the Release PR.
5. `release-please` will create a tag (e.g., `v1.2.0`) and a GitHub Release.
6. The `build-release.yml` workflow will build and sign the APK and attach it to the GitHub Release.
7. Download the APK from the GitHub Release and upload it to the Hammerhead Extension Library dashboard.

## Required GitHub Secrets Summary

| Secret | Description |
|--------|-------------|
| `SIGNING_KEYSTORE_BASE64` | Base64-encoded release keystore file |
| `SIGNING_KEYSTORE_PASSWORD` | Keystore password |
| `SIGNING_KEY_ALIAS` | Key alias (`karoo-radar`) |
| `SIGNING_KEY_PASSWORD` | Key password (same as keystore password for PKCS12) |
