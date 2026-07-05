# release-automation Specification

## Purpose
TBD - created by archiving change setup-release-pipeline. Update Purpose after archive.
## Requirements
### Requirement: release-please creates Release PRs from conventional commits
The project SHALL run a GitHub Actions workflow on every push to `main` that invokes release-please to create or update a Release PR based on conventional commits since the last release.

#### Scenario: A feature commit lands on main
- **WHEN** a commit with message `feat: add threat audio alert` is pushed to `main`
- **THEN** release-please creates or updates a Release PR proposing a minor version bump

#### Scenario: A fix commit lands on main
- **WHEN** a commit with message `fix: correct distance rounding` is pushed to `main`
- **THEN** release-please creates or updates a Release PR proposing a patch version bump

### Requirement: release-please updates version and changelog
The Release PR SHALL update `version.txt`, `CHANGELOG.md`, and `.release-please-manifest.json`. `app/build.gradle.kts` derives `versionCode` from the updated `versionName`.

#### Scenario: Release PR is merged
- **WHEN** the Release PR is merged
- **THEN** `CHANGELOG.md` contains a new section for the release, `version.txt` matches the release tag, and `versionCode` derived in Gradle is incremented

### Requirement: release-please creates GitHub Releases and tags
Upon merging a Release PR, release-please SHALL create a git tag and a GitHub Release with generated release notes.

#### Scenario: Release PR merged for version 1.1.0
- **WHEN** the Release PR for `v1.1.0` is merged
- **THEN** a git tag `v1.1.0` and a GitHub Release named `v1.1.0` exist

### Requirement: CI builds and attaches signed release APK on tags
The project SHALL run a GitHub Actions workflow on every tag matching `v*.*.*` that builds a signed release APK and attaches it to the matching GitHub Release.

#### Scenario: Tag v1.1.0 is pushed
- **WHEN** the tag `v1.1.0` is pushed
- **THEN** the workflow produces a signed release APK and uploads it as a release asset

### Requirement: Store metadata is versioned
The project SHALL include a `store/` directory containing Hammerhead Extension Library listing text and a `screenshots/` subdirectory.

#### Scenario: Maintainer prepares a release
- **WHEN** a maintainer reviews the release assets
- **THEN** `store/` contains listing text and screenshot placeholders ready for manual upload

