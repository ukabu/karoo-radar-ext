## Context

The `build-release.yml` workflow uploads `app/build/outputs/apk/release/*.apk`. Gradle always names the output `app-release.apk` for an app module named `app`. The `action-gh-release` step uploads this file verbatim, so the release asset ends up with the generic name.

## Goals / Non-Goals

**Goals:**
- Ensure every release APK has the filename `karoo-radar-<version>-release.apk`.

**Non-Goals:**
- Changing the Gradle module name or output file.
- Changing release-please behavior.

## Decisions

### 1. Rename after build, before upload
The simplest, least invasive approach is to rename the APK in the workflow using the tag ref. The release event provides `${{ github.ref_name }}`, which is the tag (e.g., `v1.1.0`). We strip the leading `v` and construct `karoo-radar-1.1.0-release.apk`. This avoids touching Gradle configuration.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| Multiple APK files in the output directory | The glob should match exactly one APK; if it ever matches more, the workflow will fail fast. |

## Migration Plan

Not applicable.

## Open Questions

None.
