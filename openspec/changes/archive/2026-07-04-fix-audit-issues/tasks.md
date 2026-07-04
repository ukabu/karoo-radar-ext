## 1. FIT Recording

- [x] 1.1 Make `FitRecorder` always write all four developer fields, using `0` for any unavailable/null value (distance, relative speed, absolute speed when vehicles = 0 or handoff/disconnect). Keep the existing `vehicleCount` value.
- [x] 1.2 Verify the `FitRecorder` still skips writes entirely when `!radar.isConnected`.

## 2. Speed Derivation

- [x] 2.1 Update `deriveRelativeSpeed` to extract timestamp from the `DataPoint` (use `elapsedTime` if available, fall back to `System.currentTimeMillis()` with a warning comment).
- [x] 2.2 Fix handoff sample logic: `deriveRelativeSpeed` should return `null` while `handoffRemainingSamples > 0`, ensuring exactly 2 ticks of `--`.
- [x] 2.3 Ensure `distanceHistory` retains samples across handoff ticks so speed derivation resumes immediately after handoff ends.
- [x] 2.4 Update `SPEED_WINDOW_MS` to `3000L` (or update docs) to align the ~3s declaration with the constant.

## 3. Color Unification

- [x] 3.1 Change `ThreatColors.toBackgroundColor()` to return an `androidx.compose.ui.graphics.Color` directly (or keep the resource ID and add a resolver).
- [x] 3.2 Update `radarDatafieldGlance` to call `ThreatColors.toBackgroundColor()` and remove the hardcoded `when` block.
- [x] 3.3 Remove unused imports (`RadarState`, `SpeedState`, `Units`, `ViewConfig`, `StateFlow`) from `RadarDatafieldGlance.kt`.

## 4. Unit Preference Caching

- [x] 4.1 Add a `SharedState` or similar object holding a `StateFlow<Boolean>` for imperial preference, initialized by reading `UserProfile` once at extension startup.
- [x] 4.2 Update `DatafieldUtils.getUseImperial` to collect from the shared flow instead of creating a new consumer each call.
- [x] 4.3 Remove the unused `config: ViewConfig` parameter from `startRadarView`.

## 5. Icon and Housekeeping

- [x] 5.1 Replace `ic_radar.xml` path data with a radar-themed vector (e.g., concentric arcs or wave-emitting antenna icon).
- [x] 5.2 Remove the redundant package-level `import com.ukabu.karooradar.ThreatLevel` in `ThreatColors.kt`.
