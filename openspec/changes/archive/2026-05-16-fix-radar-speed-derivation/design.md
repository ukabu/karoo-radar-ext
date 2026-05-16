## Context

The `RadarProcessor` class derives relative vehicle speed from radar range data and computes absolute vehicle speed by adding cyclist ground speed. During a real test ride two issues were discovered:

1. **Absolute speed always `--`**: `subscribeSpeed()` read `DataType.Field.SINGLE` from the `SPEED` stream, but the Karoo SDK stores cyclist speed under `DataType.Field.SPEED`. The wrong key always returned `null`, making `SpeedState.speedKmh` null and therefore `absoluteSpeedKmh` null.

2. **Relative speed wildly spiky**: `deriveRelativeSpeed()` computed `-dD/dt` between every consecutive radar sample using `System.currentTimeMillis()`. Radar updates can arrive at sub-second intervals; tiny `dt` amplified distance measurement noise into huge instantaneous speed values. The EMA (α=0.3) could not suppress these spikes because each input was already garbage.

## Goals / Non-Goals

**Goals:**
- Restore absolute speed display by reading the correct SDK field.
- Stabilize relative speed display by deriving it over a longer time window.
- Keep handoff behavior (show `--` for 1–2 samples when the closest vehicle changes slot) unchanged.
- Preserve existing FIT recording behavior — no schema changes.

**Non-Goals:**
- No new datafields, no UI redesign, no new user settings.
- No changes to threat-level or background-color logic.
- No attempt to read native radar relative speed (SDK does not expose it).

## Decisions

### Use a 2-second sliding window instead of point-to-point derivative
**Rationale**: Averaging distance change over ~1–2 seconds matches the radar's update cadence and naturally smooths high-frequency noise before the derivative is taken. This is simpler than adding a separate low-pass filter and does not introduce additional tuning parameters.
**Alternative considered**: Increasing the EMA alpha or adding a Kalman filter — rejected because the root cause was the derivative window, not the smoothing stage.

### Keep the existing EMA on top of the windowed derivative
**Rationale**: The EMA still provides useful smoothing for gradual speed changes. The window handles the noise; the EMA handles gentle transitions. The two stages are orthogonal.

### Window boundaries: 2000 ms window, 1000 ms minimum dt
**Rationale**: 2 seconds captures enough samples at typical radar update rates (~2–4 Hz). Requiring ≥ 1 second of span guarantees a meaningful distance delta. If the window is too short, we fall back to the previous EMA value rather than computing a noisy derivative.

## Risks / Trade-offs

- **[Slightly slower initial speed display]** → The first valid speed now needs ~1 second of history instead of appearing immediately. Mitigation: riders care more about stable readings than instantaneous first-frame values.
- **[Memory]** → `ArrayDeque<DistanceSample>` holds at most a few dozen samples. Negligible impact.
