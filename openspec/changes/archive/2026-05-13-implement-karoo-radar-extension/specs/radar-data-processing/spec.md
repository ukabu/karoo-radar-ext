## ADDED Requirements

### Requirement: Subscribe to RADAR data stream
The extension SHALL subscribe to `DataType.Type.RADAR` and read `RADAR_TARGET_1_RANGE` through `RADAR_TARGET_8_RANGE`, `RADAR_THREAT_LEVEL`, and `RADAR_ERROR`.

#### Scenario: Radar connected and transmitting
- **WHEN** an ANT+ radar is paired and active
- **THEN** the extension receives updates at the stream's emission rate

### Requirement: Vehicle count from non-zero ranges
Vehicle Count SHALL be computed as the count of non-zero values among `RADAR_TARGET_1_RANGE` through `RADAR_TARGET_8_RANGE`.

#### Scenario: Three vehicles detected
- **WHEN** three of the eight target range fields are non-zero
- **THEN** Vehicle Count is `3`

### Requirement: Closest vehicle selected as minimum non-zero range
Closest Vehicle Distance SHALL be the minimum non-zero value among `RADAR_TARGET_1_RANGE` through `RADAR_TARGET_8_RANGE`, expressed in meters.

#### Scenario: Multiple vehicles at different distances
- **WHEN** target ranges report `[150, 0, 80, 0, 0, 0, 0, 0]` in meters
- **THEN** Closest Vehicle Distance is `80`

### Requirement: Relative speed derived from distance deltas
Closest Vehicle Relative Speed SHALL be derived from successive distance samples using a smoothed exponential moving average of `-dD/dt` over approximately 3 seconds. The value SHALL be capped at 200 km/h. Negative computed values SHALL be clamped to 0.

#### Scenario: Vehicle approaching at constant speed
- **WHEN** the closest vehicle distance decreases by 30 meters over 3 seconds
- **THEN** the derived relative speed stabilizes near 36 km/h

### Requirement: Handoff detection resets speed window
When the identity of the closest vehicle changes (detected by an increasing-then-decreasing distance discontinuity or a new minimum from a different target slot), the derivation window SHALL reset and the Relative Speed datafield SHALL display `--` for 1–2 seconds.

#### Scenario: One vehicle passes, another appears closer
- **WHEN** the closest target slot changes and distance jumps discontinuously
- **THEN** relative speed shows `--` for 1–2 seconds before the new EMA converges

### Requirement: Absolute speed from cyclist ground speed plus relative speed
Closest Vehicle Absolute Speed SHALL be computed as `relative_speed + cyclist_speed` using cyclist ground speed from the Karoo `SPEED` stream. When cyclist speed is unavailable, the datafield SHALL display `--`.

#### Scenario: Cyclist moving at 30 km/h, vehicle approaching at 20 km/h relative
- **WHEN** cyclist speed is 30 km/h and relative speed is 20 km/h
- **THEN** absolute speed displays `50`
