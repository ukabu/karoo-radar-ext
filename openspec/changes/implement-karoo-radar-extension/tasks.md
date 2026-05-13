## 1. Build Setup

- [ ] 1.1 Initialize Android Gradle project with `app` module, Kotlin, and `karoo-ext` SDK dependency
- [ ] 1.2 Configure `AndroidManifest.xml` with extension metadata and required permissions
- [ ] 1.3 Add application icon resources and extension display name strings
- [ ] 1.4 Verify `./gradlew assembleDebug` produces an APK

## 2. Domain Model

- [ ] 2.1 Create `RadarState` data class with vehicle count, closest distance, threat level, relative speed, absolute speed, isConnected, and target slot index
- [ ] 2.2 Create `SpeedState` data class for cyclist ground speed with availability flag
- [ ] 2.3 Define threat level color constants (CLEAR, APPROACHING, WARNING, CRITICAL)
- [ ] 2.4 Add unit conversion utilities (meters ↔ feet, km/h ↔ mph)

## 3. Radar Data Processing

- [ ] 3.1 Create `RadarProcessor` class subscribing to `DataType.Type.RADAR` and exposing `StateFlow<RadarState>`
- [ ] 3.2 Parse `RADAR_TARGET_1_RANGE`…`RADAR_TARGET_8_RANGE` into vehicle count (non-zero count) and closest distance (min non-zero)
- [ ] 3.3 Read `RADAR_THREAT_LEVEL` into `RadarState`
- [ ] 3.4 Track target slot index of closest vehicle for handoff detection
- [ ] 3.5 Implement EMA-based relative speed derivation with ~3-second window, 200 km/h cap, and negative clamp
- [ ] 3.6 Implement handoff detection: reset EMA and emit `--` for 1–2 seconds when closest slot changes discontinuously
- [ ] 3.7 Subscribe to `DataType.Type.SPEED` for cyclist ground speed and compute absolute speed

## 4. Datafield Display

- [ ] 4.1 Create base Glance composable for shared layout (label + value, background color, size-agnostic)
- [ ] 4.2 Implement `VehicleCountDataType` with localized label and `0` / count display
- [ ] 4.3 Implement `ClosestDistanceDataType` with unit-aware display and `--` for no vehicle
- [ ] 4.4 Implement `RelativeSpeedDataType` with unit-aware display and `--` for no vehicle / handoff
- [ ] 4.5 Implement `AbsoluteSpeedDataType` with unit-aware display and `--` when cyclist speed unavailable
- [ ] 4.6 Wire all four datafields to observe `RadarProcessor.radarState` and `SpeedState`
- [ ] 4.7 Add string resources for labels in English (localization placeholder)

## 5. FIT Recording

- [ ] 5.1 Define developer field schema in `KarooExtension` with four scalar fields (uint8/uint16)
- [ ] 5.2 Create `FitRecorder` subscribing to `RideState` and `RadarState`
- [ ] 5.3 Write developer fields at ~1 Hz only when `RideState.Recording` AND radar connected
- [ ] 5.4 Skip writes on paused ride or radar disconnect (no placeholders)
- [ ] 5.5 Store SI units in FIT (meters, km/h) regardless of display preference

## 6. Extension Wiring

- [ ] 6.1 Create `RadarExtension` subclass of `KarooExtension` with proper lifecycle
- [ ] 6.2 Initialize `RadarProcessor` and `FitRecorder` in `onCreate`
- [ ] 6.3 Register all four `DataType` entries in extension metadata
- [ ] 6.4 Call `startFit()` and bind developer fields in extension setup

## 7. Deployment

- [ ] 7.1 Create `deploy.sh` script: build debug APK + `adb install -r` + echo next steps
- [ ] 7.2 Test deployment to Karoo 3 and verify extension appears in system settings
