## ADDED Requirements

### Requirement: Project uses Gradle with karoo-ext SDK
The project SHALL use the Gradle build system with the Android plugin and depend on the Hammerhead `karoo-ext` SDK.

#### Scenario: SDK dependency present
- **WHEN** the project is built
- **THEN** the `karoo-ext` SDK is resolved as a dependency and the build succeeds

### Requirement: Build produces deployable APK
The Gradle build SHALL produce an APK artifact suitable for sideloading onto a Karoo 3 device.

#### Scenario: Assemble release
- **WHEN** the `./gradlew assembleRelease` task is executed
- **THEN** an APK file is emitted to `build/outputs/apk/release/`

### Requirement: Deployment script sideloads to Karoo
The project SHALL include an ADB-based deployment script that installs the APK onto a connected Karoo 3 device.

#### Scenario: Karoo connected via USB
- **WHEN** the deployment script is run and a Karoo 3 is connected via ADB
- **THEN** the extension APK is installed and the Karoo system prompts to enable it
