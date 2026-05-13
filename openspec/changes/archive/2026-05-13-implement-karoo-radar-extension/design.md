## Context

This is a new Karoo 3 Extension project. There is no existing codebase. The Hammerhead Karoo SDK (`karoo-ext`) provides an Android-based extension framework with Glance for custom datafield rendering, Kotlin Flow-based data streams, and FIT recording APIs. Prior art (`eiradar`) demonstrates the patterns for radar stream consumption, Glance widgets, and FIT writes.

The Karoo SDK exposes only scalar radar fields: `RADAR_TARGET_1_RANGE` through `RADAR_TARGET_8_RANGE`, `RADAR_THREAT_LEVEL`, and `RADAR_ERROR`. No raw relative speed, no per-vehicle IDs, no array developer fields. Target device is Karoo 3 only.

## Goals / Non-Goals

**Goals:**
- Build a complete, deployable Karoo 3 Extension with four datafields and FIT recording
- Derive closest-vehicle metrics from the limited SDK radar fields
- Provide smooth relative speed via EMA with handoff detection
- Support metric/imperial display units
- Include one-command ADB deployment to a test Karoo

**Non-Goals:**
- Karoo 2 support
- Per-vehicle arrays (impossible with SDK)
- Settings screen or user-configurable thresholds
- Audio/visual alerts (Karoo handles these natively)
- mybiketraffic FIT compatibility (SDK does not support their array schema)
- `WriteToLapMesg` (not supported by SDK)

## Decisions

### Architecture: Extension + Datafield classes + Shared RadarState
Following the `eiradar` pattern, the extension registers a `KarooExtension` subclass for lifecycle and FIT initialization. Each datafield is a separate class extending `DataTypeImpl` with a Glance composable. Processing logic lives in a shared `RadarProcessor` that consumes the `RADAR` stream and exposes a `StateFlow<RadarState>`. Datafields observe this flow and render from it.

**Why**: Decouples stream processing from rendering; single subscription to the RADAR stream regardless of how many datafields are visible.

### Relative Speed Derivation: EMA with 3-second window
Single-pole IIR filter (exponential moving average) over instantaneous `-dD/dt` samples. Alpha tuned for ~3-second settling. Value capped at 200 km/h, negative clamped to 0. Handoff detected by tracking the target slot index of the closest vehicle; on change, reset the EMA window and emit `--` for 1–2 seconds (two samples at typical ~1 Hz).

**Why**: The SDK provides no raw speed. A true moving average would need a ring buffer; EMA is simpler and reacts faster to genuine changes. Handoff detection prevents showing garbage during transitions.

**Alternative considered**: Simple `delta / delta_t` over fixed 1-second windows. Rejected because it produces noisy, jumpy values.

### FIT Gating: Combined `RideState` + `isRadarConnected`
A `FitRecorder` class subscribes to both `RideState` and `RadarState`. It emits developer fields only when `RideState.Recording` AND `isRadarConnected` are true. On disconnect or pause, it skips the write for that cycle. No placeholders or zero-fills.

**Why**: Matches the spec exactly and avoids phantom data in the FIT file.

### Absolute Speed: Requires SPEED stream subscription
The `RadarProcessor` also subscribes to `DataType.Type.SPEED` for cyclist ground speed. When unavailable, absolute speed shows `--` in datafield and writes `0xFF` (FIT "invalid" uint8) or skips. When available, computes `relative + cyclist`.

**Why**: The spec requires cyclist speed for absolute speed. The SDK SPEED stream is the canonical ground speed source.

### Build: Gradle + karoo-ext SDK via jitpack/maven
Use standard Android Gradle Plugin with `karoo-ext` as a dependency. The SDK is published to Maven Central or GitHub Packages.

**Why**: Standard Android tooling. Hammerhead provides Gradle examples.

### Deployment: Shell script wrapping `adb install`
A `deploy.sh` script runs `./gradlew assembleDebug` then `adb install -r` onto the connected Karoo.

**Why**: Simple, no additional tooling needed. Karoo 3 supports ADB sideloading.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| EMA-derived speed is an approximation, not ground truth | Documented in CONTEXT.md; capped and clamped to avoid absurd values |
| Handoff detection may false-positive on noisy range data | Two-sample `--` window is short; user still gets distance data immediately |
| SDK may change `karoo-ext` APIs in future versions | Pin to a specific SDK version in Gradle |
| FIT file size increase from 4 extra fields per record | Negligible; fields are small scalar uints |
| Karoo system display unit preference API may be undocumented | Reverse-engineered from `eiradar`; falls back to metric if unavailable |

## Migration Plan

N/A — new project. Deployment is via `deploy.sh` which installs over any prior build.

## Open Questions

1. Exact `karoo-ext` SDK version and Maven coordinates to use — verify latest stable from `https://github.com/hammerheadnav/karoo-ext`
2. `DataTypeImpl` subclassing pattern — verify against latest SDK; `eiradar` may use an older pattern
3. Glance `LocalSize` or size-agnostic composable API — confirm exact composable signature for Karoo datafields
