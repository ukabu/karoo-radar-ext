## Why

The first official release (`v1.1.0`) uploaded an APK named `app-release.apk` to the GitHub Release. This generic name is hard to identify and does not match our earlier decision to produce versioned APK filenames like `karoo-radar-1.2.3-release.apk`.

## What Changes

- Update the `build-release.yml` workflow to rename the built APK to `karoo-radar-<version>-release.apk` before uploading it to the GitHub Release.
- Use the git tag name to derive the version portion of the filename.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `release-automation`: the release workflow SHALL upload the APK with a versioned filename.

## Impact

- `.github/workflows/build-release.yml` only.
