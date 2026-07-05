## 1. Keystore Generation and Documentation

- [x] 1.1 Generate a fresh Android release keystore (RSA 2048, PKCS12, 25-year validity, alias `karoo-radar`).
- [x] 1.2 Create `.gitignore` entries (or verify existing) for `*.jks`, `*.keystore`, and password files.
- [x] 1.3 Add keystore backup instructions to `README.md` or `docs/releasing.md`.

## 2. Gradle Release Signing

- [x] 2.1 Add a `release` signing config to `app/build.gradle.kts` reading from environment variables.
- [x] 2.2 Update the `release` build type to use the new `release` signing config.
- [x] 2.3 Verify `./gradlew assembleRelease` works in CI when secrets are present and does not break local debug builds.

## 3. release-please Configuration

- [x] 3.1 Create `release-please-config.json` with generic updater targeting `version.txt` for `versionName` and derived `versionCode`.
- [x] 3.2 Create `.release-please-manifest.json` with initial version matching current `versionName`.
- [x] 3.3 Create `.github/workflows/release-please.yml` running on pushes to `main`.
- [x] 3.4 Add `CHANGELOG.md` template or initial file.

## 4. Build and Attach Release APK

- [x] 4.1 Create `.github/workflows/build-release.yml` triggered on tags `v*.*.*`.
- [x] 4.2 Decode `SIGNING_KEYSTORE_BASE64` to a temporary keystore file in the workflow.
- [x] 4.3 Build release APK with `./gradlew assembleRelease` using signing secrets.
- [x] 4.4 Upload the signed APK to the matching GitHub Release.

## 5. Store Metadata and Documentation

- [x] 5.1 Create `store/` directory with `description.txt`, `short_description.txt`, and `screenshots/` placeholder.
- [x] 5.2 Update `README.md` with maintainer release instructions (merge Release PR, tag triggers build, manual Hammerhead upload).
- [x] 5.3 Document required GitHub Secrets in `README.md`.

## 6. Verification

- [x] 6.1 Run `./gradlew test` and `./gradlew assembleDebug` to ensure no regressions.
- [x] 6.2 Validate workflow YAML files with `actionlint` or similar if available. (actionlint unavailable; YAML syntax validated with Python)
- [x] 6.3 Verify release-please config parses correctly (e.g., via dry-run if tooling allows). (JSON valid; CLI reached GitHub API, confirming config parse)
