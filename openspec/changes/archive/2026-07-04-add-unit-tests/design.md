## Context

The Karoo Radar Extension currently has no automated test coverage. The core logic lives in `RadarProcessor` (vehicle aggregation, speed derivation) and `FitRecorder` (FIT emission gating), with smaller helpers in `ThreatColors`, `Units`, and `ThreatLevel`. Most of these classes depend directly on Karoo SDK classes (`KarooSystemService`, `DataType.Field`, `DeveloperField`, `RideState`) that are awkward to instantiate in JVM unit tests.

## Goals / Non-Goals

**Goals:**
- Add JUnit 5 and MockK to the project.
- Achieve meaningful unit-test coverage for pure business logic without requiring a running Karoo or Android runtime.
- Keep production code changes minimal and behavior-preserving.

**Non-Goals:**
- UI/Glance rendering tests.
- Integration tests against the real Karoo SDK.
- Refactoring the entire architecture just for testability.

## Decisions

### 1. Test stack: JUnit 5 + MockK
JUnit 5 is the modern standard for Kotlin JVM tests. MockK lets us mock Kotlin classes and objects cleanly, including the Karoo SDK types we do not control.

### 2. Keep Android framework out of unit tests
We will use the standard `test` source set (JVM-only), not `testDebug`. Any test that needs Android classes (e.g., `Context`, `Resources`) is out of scope for this change.

### 3. Extract pure helpers from `RadarProcessor`
`RadarProcessor` mixes stream subscription with computation. To test the computation without mocking `KarooSystemService`, we will extract:
- `computeVehicleState(ranges: List<Double>, threatValue: Int): VehicleState` — returns count, closest distance, and threat level.
- `deriveRelativeSpeed(...)`, `deriveAbsoluteSpeed(...)` as pure functions operating on values rather than internal mutable state.

The public `RadarProcessor` API and behavior remain unchanged; only internal structure is adjusted.

### 4. Test `FitRecorder` with a fake `RadarProcessor` and mocked `KarooSystemService`
`FitRecorder` combines state flows and emits `FitEffect`. We will provide a fake `RadarProcessor` with a mutable `radarState` flow and mock `karooSystem.consumerFlow<RideState>()` to verify that `WriteToRecordMesg` is emitted (or skipped) under the right gating conditions.

### 5. Test helpers directly
`Units`, `ThreatLevel.fromInt(...)`, and `ThreatColors.toBackgroundColor(...)` are already pure and will be tested directly.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| Extracting helpers changes code organization slightly. | Keep methods internal where possible; do not alter public behavior. |
| MockK can be slow with large object graphs. | Only mock the narrow Karoo SDK interfaces needed for each test. |
| Some logic remains hard to unit test (e.g., coroutine timing). | Cover timing-sensitive behavior through the extracted pure functions; leave integration timing for future tests. |

## Migration Plan

Not applicable. No production behavior changes.

## Open Questions

None.
