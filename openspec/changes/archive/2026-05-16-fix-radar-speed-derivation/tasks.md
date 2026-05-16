## 1. Diagnose

- [x] 1.1 Investigate why absolute speed always shows `--`
- [x] 1.2 Investigate why relative speed jumps wildly during approach

## 2. Fix Absolute Speed

- [x] 2.1 Change `subscribeSpeed()` field key from `DataType.Field.SINGLE` to `DataType.Field.SPEED`
- [x] 2.2 Verify `SpeedState.speedKmh` is no longer null when speed stream is active

## 3. Fix Relative Speed Derivation

- [x] 3.1 Add `DistanceSample` data class and `ArrayDeque<DistanceSample>` sliding window
- [x] 3.2 Replace point-to-point derivative in `deriveRelativeSpeed()` with 2-second windowed derivative
- [x] 3.3 Update `resetEma()` to clear the window alongside resetting EMA state
- [x] 3.4 Add `SPEED_WINDOW_MS = 2000L` and `MIN_SPEED_DT_MS = 1000L` constants

## 4. Verify

- [x] 4.1 Build debug APK with `./gradlew :app:assembleDebug`
- [x] 4.2 Confirm no compile errors in `RadarProcessor.kt`
