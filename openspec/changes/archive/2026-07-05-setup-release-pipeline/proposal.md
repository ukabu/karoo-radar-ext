## Why

The Karoo Radar Extension currently uses a debug-only build script (`deploy.sh`) that side-loads to a connected Karoo. To publish official releases through the Hammerhead Extension Library, we need a repeatable, versioned release pipeline that produces signed release APKs, maintains a changelog, and creates GitHub Releases from which the APK can be manually uploaded to Hammerhead.

## What Changes

- Generate a fresh Android release keystore for signing production APKs.
- Configure release signing in `app/build.gradle.kts` using environment variables/secrets.
- Add a `release-please` GitHub Actions workflow that creates Release PRs from conventional commits.
- Add a build-and-sign GitHub Actions workflow triggered by version tags.
- Initialize `CHANGELOG.md` and `.release-please-manifest.json` for release-please.
- Add store metadata files (description, screenshots directory) for the Hammerhead Extension Library listing.
- Update `README.md` with release instructions for maintainers.

## Capabilities

### New Capabilities
- `release-automation`: Automated versioning, changelog generation, and GitHub Release creation driven by conventional commits and release-please.
- `release-signing`: Secure release APK signing using a dedicated keystore and GitHub Secrets.

### Modified Capabilities
None. No product behavior changes.

## Impact

- `.github/workflows/`: new release-please and build-release workflows.
- `app/build.gradle.kts`: release signing config and version management.
- `gradle/libs.versions.toml`: no changes.
- `CHANGELOG.md`, `.release-please-manifest.json`, `release-please-config.json`: new release-please files.
- `store/`: new directory for Hammerhead listing metadata and screenshots.
- `README.md`: updated maintainer release instructions.
- A fresh release keystore will be generated locally and stored in GitHub Secrets; the keystore file itself will **not** be committed.
