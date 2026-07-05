## 1. Rename APK in Release Workflow

- [x] 1.1 Add a workflow step that renames `app-release.apk` to `karoo-radar-<version>-release.apk` using the tag name.
- [x] 1.2 Update the `action-gh-release` upload pattern to use the renamed file.

## 2. Verification

- [x] 2.1 Run `./gradlew test` to ensure no regressions.
- [x] 2.2 Validate workflow YAML syntax.
