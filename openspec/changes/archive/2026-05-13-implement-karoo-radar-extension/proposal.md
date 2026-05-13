## Why

Karoo 3 has a native radar side-view but no numerical datafields for vehicle count, distance, or speed, and no FIT recording of radar data for post-ride analysis. Cyclists who want quantitative radar metrics or review their rides in analysis tools need this data exposed and recorded.

## What Changes

Create a Hammerhead Karoo 3 Extension (`karoo-radar-ext`) that:

- Registers four selectable ride datafields: Vehicle Count, Closest Vehicle Distance, Closest Vehicle Relative Speed, Closest Vehicle Absolute Speed
- Subscribes to the ANT+ radar data stream via the Karoo Extension SDK and parses target ranges and threat level
- Derives relative speed from distance deltas using a smoothed exponential moving average with handoff detection
- Computes absolute speed from cyclist ground speed + relative speed
- Records four scalar developer fields to the ride FIT file at 1 Hz during active recording
- Drives datafield background color by threat level (yellow/orange/red)
- No settings screen — all behavior is hardcoded

This is a new project; there is no existing codebase to modify.

## Capabilities

### New Capabilities
- `build-setup`: Project build configuration with Gradle, `karoo-ext` SDK, Android plugin, and deployment flow
- `datafield-display`: Four selectable Glance datafield widgets with localized labels, unit-aware display, and threat-level background coloring
- `radar-data-processing`: ANT+ radar stream subscription, target range parsing, closest-vehicle selection, relative speed derivation (EMA, handoff detection), and absolute speed computation
- `fit-recording`: Scalar developer field schema and 1 Hz FIT emission gated by `RideState.Recording` and radar connection state

### Modified Capabilities
- None (new project)

## Impact

- New Android/Kotlin project consuming the Hammerhead `karoo-ext` SDK
- Targets Karoo 3 only; minimum SDK determined by `karoo-ext` requirements
- Custom Gradle build configuration for Karoo extension packaging and deployment
- FIT file records use the extension's own developer field schema (not mybiketraffic-compatible)
