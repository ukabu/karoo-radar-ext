# release-signing Specification

## Purpose
TBD - created by archiving change setup-release-pipeline. Update Purpose after archive.
## Requirements
### Requirement: A release keystore is generated for production signing
The project SHALL have a freshly generated Android release keystore used exclusively for signing release APKs distributed to users.

#### Scenario: Keystore is generated
- **WHEN** the keystore is created
- **THEN** it uses RSA 2048-bit key, PKCS12 format, 25-year validity, and alias `karoo-radar`

### Requirement: Keystore is not committed to the repository
The keystore file and its passwords SHALL NOT be committed to version control.

#### Scenario: Repository is inspected
- **WHEN** someone views the source tree
- **THEN** no `.jks`, `.keystore`, or password files are present

### Requirement: Signing secrets are stored in GitHub
The keystore file (base64-encoded) and its credentials SHALL be stored as GitHub repository secrets.

#### Scenario: CI workflow runs
- **WHEN** the release build workflow executes
- **THEN** it reconstructs the keystore from `SIGNING_KEYSTORE_BASE64` and signs the APK using `SIGNING_KEYSTORE_PASSWORD`, `SIGNING_KEY_ALIAS`, and `SIGNING_KEY_PASSWORD`

### Requirement: Gradle release build uses signing secrets
`app/build.gradle.kts` SHALL configure a `release` signing block that reads the keystore path, password, alias, and key password from environment variables.

#### Scenario: CI builds release APK
- **WHEN** `./gradlew assembleRelease` runs in CI with secrets present
- **THEN** Gradle signs the resulting APK with the release keystore

#### Scenario: Local debug build
- **WHEN** a developer builds the debug variant locally without signing secrets
- **THEN** the build succeeds without requiring the release keystore

### Requirement: Keystore backup is documented
The project documentation SHALL include instructions for securely backing up the release keystore.

#### Scenario: New maintainer joins
- **WHEN** a maintainer reads the release documentation
- **THEN** they know where the keystore is backed up and how to restore it

