## Context

The project currently produces debug APKs through `deploy.sh` and has no versioning, changelog, or signed release process. The Hammerhead Extension Library expects a signed release APK plus listing metadata. The commit history already follows Conventional Commits, making release-please a natural fit.

## Goals / Non-Goals

**Goals:**
- Generate a fresh release keystore and configure Gradle to sign release APKs with secrets.
- Automate versioning and changelog generation via release-please.
- Automate GitHub Release creation and APK attachment on version tags.
- Provide a location for Hammerhead store metadata and screenshots.

**Non-Goals:**
- Automating upload to the Hammerhead dashboard (manual per project decision).
- Publishing to Google Play Store.
- Adding local commit message enforcement (husky/commitlint).
- Changing product behavior or datafield logic.

## Decisions

### 1. release-please with generic file updater for Gradle
Android Gradle projects are not a built-in release-please release type. We will use release-please's generic updater to patch `versionCode` and `versionName` lines directly in `app/build.gradle.kts`. This avoids adding a Gradle plugin solely for version parsing and keeps the configuration transparent.

### 2. Two-workflow design
Separate concerns:
- `release-please.yml` runs on pushes to `main`, maintains the Release PR, and creates the GitHub Release + tag.
- `build-release.yml` runs on tag pushes matching `v*.*.*`, builds the release APK, signs it, and uploads the APK to the matching GitHub Release.

This separation is idiomatic for release-please: it creates releases, and a second workflow reacts to the created tag.

### 3. Release signing via GitHub Secrets
A fresh PKCS12 keystore will be generated locally. The binary keystore will be base64-encoded and stored as `SIGNING_KEYSTORE_BASE64`. The passwords and alias will be stored as separate secrets. Gradle's release signing block will read these from environment variables so CI can sign while local builds fall back gracefully or skip signing.

### 4. Keystore generation parameters
- Algorithm: RSA
- Key size: 2048 bits
- Validity: 25 years (9125 days)
- Alias: `karoo-radar`
- Format: PKCS12 (`.jks` extension for compatibility)

### 5. Version strategy
release-please manages `versionName` in a `version.txt` file. `app/build.gradle.kts` reads `version.txt` at configuration time and derives `versionCode` from the semantic version as `major * 10000 + minor * 100 + patch`. This avoids complex Gradle patching while keeping both values deterministic and version-controlled.

### 6. Store metadata
A `store/` directory will hold Hammerhead listing content: description, feature graphic placeholder, and a `screenshots/` subdirectory. This keeps listing content versioned without bloating the repo.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| Keystore is lost, blocking updates. | Document backup procedure; store keystore in password manager; keep local backup offline. |
| Release-please fails to parse `build.gradle.kts`. | Use a targeted regex in the generic updater and verify with a dry-run. |
| CI secrets are not configured before first release. | Add a setup checklist in the README; fail the build workflow with a clear error if secrets are missing. |
| Manual Hammerhead upload delays release. | Document exact steps in README; attach APK clearly to GitHub Release. |

## Migration Plan

Not applicable. This is a new pipeline with no existing release process to migrate.

## Open Questions

None.
