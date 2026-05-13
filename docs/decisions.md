# Design Decisions — Karoo Radar Extension

## What We're Building

A Karoo Extension for Hammerhead Karoo 3 that adds four on-screen datafields for rear radar (e.g., Garmin Varia) and records the data in the FIT file. The Karoo already handles visual/audio alerts natively; we focus purely on **numerical display + post-ride recording**.

---

## Decisions Made

### 1. Metrics
Display and record four metrics:

- **Vehicle Count** — total detected vehicles
- **Closest Vehicle Distance** — meters to nearest vehicle
- **Closest Vehicle Relative Speed** — derived approximation (km/h)
- **Closest Vehicle Absolute Speed** — cyclist speed + relative speed (km/h)

### 2. Scope — Closest Vehicle Only

- One closest vehicle per instant, plus total count.
- Not full per-vehicle arrays (the mybiketraffic approach). Arrays are impossible with the Karoo SDK.
- Total count is used for Vehicle Count datafield.

### 3. No-Vehicle Display

- When radar detects zero vehicles: count shows `0`, all other fields show `--` (not `0`), background turns neutral.

### 4. Relative Speed Derivation

- **Derived**, not directly exposed by Karoo SDK.
- Computed as a smoothed exponential moving average of `-dD/dt` over ~3 seconds.
- Capped at 200 km/h; negative values clamped to 0.
- Window resets and shows `--` for 1–2 seconds when the closest vehicle changes (**handoff detection**).

### 5. Absolute Speed

- Requires cyclist ground speed from the Karoo `SPEED` stream.
- Shows `--` when GPS speed is unavailable.
- Computed as `relative_speed + cyclist_speed`.

### 6. Units

- Display follows the Karoo system preference (metric/imperial).
- Whole-number display only.
- FIT records store SI units regardless (meters, km/h).

### 7. Visual Design

- **Four separate selectable datafields** — user adds whichever they want to their ride profile.
- **Single size-agnostic Glance composable** per datafield.
- Layout: small label + large centered number.
- Background color changes by threat level:
  - **CLEAR** → neutral (no override)
  - **APPROACHING** → yellow
  - **WARNING** → orange
  - **CRITICAL** → red
- When radar disconnected: all fields show `--`, neutral background.

### 8. Settings

- **No settings screen.** Hardcoded behavior only.

### 9. FIT Recording

- Own scalar developer field schema (not mybiketraffic-compatible):
  - `radar_vehicles` (uint8)
  - `radar_nearest_distance_m` (uint16)
  - `radar_relative_speed_kmh` (uint8)
  - `radar_absolute_speed_kmh` (uint8)
- Paused rides → stop writing fields.
- Radar disconnect mid-ride → skip writing fields for that second.
- Subscribes to `RideState` to gate emission.

### 10. Target

- Hammerhead Karoo 3 only. No Karoo 2 testing.

---

## Key Technical Constraints Acknowledged

| Constraint | Source | Impact |
|---|---|---|
| Karoo SDK does not expose radar relative speed | `DataType.kt` source | Must derive from distance deltas |
| Karoo SDK does not support array developer fields | `FitEffect.kt` source | Cannot match mybiketraffic array schema |
| Karoo SDK does not support `WriteToLapMesg` | SDK API surface | Cannot record per-lap or per-session aggregates |
| Karoo SDK only exposes 8 target range fields | `DataType.Field` constants | Vehicle count = count of non-zero ranges |

---

## Next Step

Implementation preparation (OpenSpec proposal / task breakdown).
