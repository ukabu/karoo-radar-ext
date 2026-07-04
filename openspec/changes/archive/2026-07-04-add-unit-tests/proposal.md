## Why

The Karoo Radar Extension currently has no automated tests. Adding unit tests for the core logic will prevent regressions, document expected behavior, and make future refactors safer — especially for the derived relative speed algorithm and FIT recording edge cases.

## What Changes

- Add JUnit 5 and MockK test dependencies to the project.
- Extract testable pure functions from `RadarProcessor` where possible (e.g., speed derivation helpers) without changing public behavior.
- Write unit tests covering:
  - Vehicle count and closest-vehicle selection from radar range arrays.
  - Threat level mapping.
  - Relative speed derivation (constant approach, handoff suppression, negative clamping, max cap).
  - Absolute speed computation (with and without cyclist speed).
  - `FitRecorder` FIT field emission gating (recording vs paused, connected vs disconnected).
  - `ThreatColors` and `Units` conversion helpers.
- Wire tests into the Gradle build so they run with `./gradlew test`.

## Capabilities

### New Capabilities
- `unit-test-coverage`: Automated unit tests exercise the core radar logic, FIT recording gating, and helper conversions.

### Modified Capabilities
None. No spec-level requirements change.

## Impact

- `build.gradle.kts`: new test dependencies and test configuration.
- `app/src/test/kotlin/com/ukabu/karooradar/`: new test files.
- Minor refactors in `RadarProcessor.kt` to expose pure helper functions for testing (no behavior change).
