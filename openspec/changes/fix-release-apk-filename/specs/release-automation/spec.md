## MODIFIED Requirements

### Requirement: CI builds and attaches signed release APK on tags
The project SHALL run a GitHub Actions workflow on every tag matching `v*.*.*` that builds a signed release APK and attaches it to the matching GitHub Release.

#### Scenario: Tag v1.2.3 is pushed
- **WHEN** the tag `v1.2.3` is pushed
- **THEN** the workflow produces a signed release APK and uploads it as a release asset named `karoo-radar-1.2.3-release.apk`

#### Scenario: Release asset is downloaded
- **WHEN** a user or maintainer downloads the APK from the GitHub Release
- **THEN** the filename identifies the extension and version (e.g., `karoo-radar-1.2.3-release.apk`) rather than the generic Gradle output name
