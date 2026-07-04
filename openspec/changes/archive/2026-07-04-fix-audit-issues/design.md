## Context

The Karoo Radar Extension has been audited and ten issues were identified across FIT recording, speed derivation, UI rendering, and code quality. None of these require new architectural patterns or external dependencies; they are implementation defects and polish items. This design documents the approach to fixing them without introducing regressions.

## Goals / Non-Goals

**Goals:**
- Ensure every FIT record contains the same four developer fields (write `0`/`0.0` when data is unavailable) for consistent schema.
- Derive relative speed using data point timestamps instead of wall-clock time to eliminate scheduling-skew errors.
- Display `--` for exactly 2 ticks during a closest-vehicle handoff.
- Deduplicate threat color logic by routing Glance through the existing `ThreatColors` helper.
- Cache the Karoo user profile unit preference to avoid creating a consumer every second per datafield.
- Replace the generic info-circle app icon with a radar-themed vector.
- Clean up unused imports and parameters.

**Non-Goals:**
- No new user-facing features or settings.
- No changes to the domain model (`RadarState`, `ThreatLevel`, etc.).
- No new dependencies.
- No spec-level requirement changes.

## Decisions

### 1. Sentinel values for missing FIT fields instead of omitting them
When `closestDistanceMeters`, `relativeSpeedKmh`, or `absoluteSpeedKmh` are null, `FitRecorder` will write `0.0` for that field rather than skipping it. `vehicleCount` already has a natural zero. This produces fixed-length developer field records regardless of radar state, which standard FIT parsers expect.

### 2. Use `DataPoint.elapsedTime` (or `System.currentTimeMillis()` fallback) for speed derivation
`DataPoint` exposes an optional `elapsedTime` field in the Karoo SDK. We will use this if present; otherwise fall back to `System.currentTimeMillis()` with a small scheduling-lag warning in a code comment. This removes the scheduling-skew risk without breaking behavior on SDK versions where the field may not be populated.

### 3. Handoff tick count fix
Current code: `resetEma()` → `handoffRemainingSamples = HANDOFF_SAMPLES` (2), decrement on first tick after handoff starts, then decrement again. After handoff ends, `deriveRelativeSpeed` sees only 1 sample and returns `null` because `emaSpeed` was reset to `0`. This yields 3 ticks of `--`. Fix: make `deriveRelativeSpeed` return early with `null` when `handoffRemainingSamples > 0`, and only allow derivation when samples are `0`. After decrement reaches `0`, history will have 2+ samples on the next tick, so speed resumes immediately. Net: exactly 2 ticks of `--`.

### 4. Shared `StateFlow<Boolean>` for unit preference
`DatafieldUtils` will expose a lazily-initialized `StateFlow<Boolean>` that reads `UserProfile` once and caches it. Each datafield view collects this flow instead of calling `getUseImperial()` every second.

### 5. Color unification: Glance reads `colors.xml` via `ThreatColors.toBackgroundColor()`
`radarDatafieldGlance` will receive the resolved background `Color` from `ThreatColors` and `LocalContext`, not hardcode ARGB. This centralizes the palette.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| Writing `0` for missing FIT fields could be misinterpreted as "vehicle at 0 m" in analysis tools. | Document the sentinel convention in CONTEXT.md; `vehicleCount = 0` already signals no vehicles, so downstream consumers should gate on count. |
| Using `DataPoint.elapsedTime` may not be present on all SDK builds. | Graceful fallback to wall-clock with a lint-level comment; no behavioral regression. |
| Changing the handoff tick count may feel slightly more/less stable to testers. | Spec already declares 1–2 seconds; the fix brings code into compliance. |
| Removing hardcoded colors could change the exact shade if `colors.xml` values differ from inline values. | Audit the two sets now — `colors.xml` already matches the inline values exactly (verified in audit). |

## Migration Plan

Not applicable. No database, API, or user-data migrations.

## Open Questions

None.
