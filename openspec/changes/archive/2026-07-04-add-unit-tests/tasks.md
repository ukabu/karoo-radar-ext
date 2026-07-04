## 1. Test Dependencies and Build Setup

- [x] 1.1 Add JUnit 5, MockK, and kotlinx-coroutines-test dependencies to `app/build.gradle.kts`.
- [x] 1.2 Configure the `test` source set to use JUnit 5 platform.
- [x] 1.3 Verify `./gradlew test` runs without errors (even if no tests exist yet).

## 2. Refactor `RadarProcessor` for Testability

- [x] 2.1 Extract a pure `computeVehicleState(ranges, threatValue)` function that returns count, closest distance, and threat level.
- [x] 2.2 Refactor relative speed derivation into a testable function that accepts distance samples and handoff state explicitly.
- [x] 2.3 Ensure public `RadarProcessor` behavior remains unchanged after extraction.

## 3. Vehicle Aggregation and Threat Tests

- [x] 3.1 Write tests for vehicle count and closest distance with multiple vehicles, single vehicle, and no vehicles.
- [x] 3.2 Write tests for `ThreatLevel.fromInt` covering valid values and out-of-range fallback.

## 4. Speed Derivation Tests

- [x] 4.1 Write test for constant approach speed converging near expected value.
- [x] 4.2 Write test for handoff suppressing speed for exactly 2 ticks.
- [x] 4.3 Write test for negative computed speed clamped to 0.
- [x] 4.4 Write test for speed capped at 200 km/h.

## 5. Absolute Speed and Helper Tests

- [x] 5.1 Write test for absolute speed when both relative and cyclist speeds are available.
- [x] 5.2 Write test for absolute speed returning null when cyclist speed is unavailable.
- [x] 5.3 Write tests for `Units.metersToFeet` and `Units.kmhToMph`.
- [x] 5.4 Write tests for `ThreatColors.toBackgroundColor`.

## 6. FIT Recorder Tests

- [x] 6.1 Set up a fake `RadarProcessor` with a mutable `radarState` flow.
- [x] 6.2 Mock `karooSystem.consumerFlow<RideState>()` to emit controlled ride states.
- [x] 6.3 Write test verifying `WriteToRecordMesg` is emitted during Recording with radar connected.
- [x] 6.4 Write test verifying emission stops when ride is Paused.
- [x] 6.5 Write test verifying emission stops when radar disconnects.

## 7. Verification

- [x] 7.1 Run `./gradlew test` and ensure all tests pass.
- [x] 7.2 Run `./gradlew :app:compileDebugKotlin` to confirm production code still compiles.
