## Why

A code audit of the current Karoo Radar Extension revealed ten issues ranging from FIT record inconsistency to UI color duplication and performance anti-patterns. Fixing these now prevents confusing post-ride data, reduces battery drain from repeated Karoo consumer creation, and eliminates visual/correctness bugs before a public release.

## What Changes

- Fix FIT developer fields to write sentinel/zeroed values when data is missing, producing consistent record schemas instead of variable-length records.
- Replace wall-clock `System.currentTimeMillis()` with data point timestamps in relative speed derivation for accurate dt even under coroutine scheduling delays.
- Correct handoff sample count so relative speed shows `--` for exactly 2 ticks (matching spec), not 3.
- Unify Glance background colors to use the existing `ThreatColors.toBackgroundColor()` helper and `colors.xml` resources instead of hardcoded ARGB duplicates.
- Cache the Karoo `UserProfile` unit preference instead of creating a new consumer every second in each datafield view.
- Remove unused imports, parameters, and redundant package-level imports.
- Replace the generic info-circle app icon with a radar-themed vector drawable.
- Align the speed derivation window constant (`SPEED_WINDOW_MS`) with documented "~3 seconds" behavior or update documentation to match.

## Capabilities

### New Capabilities
None. This change is a pure bug-fix and polish pass.

### Modified Capabilities
None. No spec-level requirements change; all fixes are implementation-only.

## Impact

- `FitRecorder.kt`: writes missing fields as `0` instead of omitting them.
- `RadarProcessor.kt`: uses `DataPoint.elapsedTime` for speed derivation; fixes handoff sample logic; updates `SPEED_WINDOW_MS`.
- `RadarDatafieldGlance.kt`: consumes `ThreatColors.toBackgroundColor()` and `colors.xml`.
- `DatafieldUtils.kt`: caches imperial/metric preference via a shared `StateFlow`.
- `ThreatColors.kt`: remove redundant import.
- `AbsoluteSpeedDataType.kt`, `RelativeSpeedDataType.kt`, `ClosestDistanceDataType.kt`: no functional changes.
- `ic_radar.xml`: new radar-themed vector icon.
- `decisions.md` or `CONTEXT.md`: minor text alignment if `SPEED_WINDOW_MS` changes.
