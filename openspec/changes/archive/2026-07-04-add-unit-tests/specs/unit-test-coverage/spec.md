## ADDED Requirements

### Requirement: Vehicle aggregation logic is unit tested
The project SHALL have unit tests covering vehicle count and closest-vehicle distance extraction from the eight `RADAR_TARGET_*_RANGE` fields.

#### Scenario: Multiple vehicles at different ranges
- **WHEN** the range array contains `[150.0, 0.0, 80.0, 0.0, 0.0, 0.0, 0.0, 0.0]`
- **THEN** vehicle count is `2` and closest distance is `80.0`

#### Scenario: No vehicles detected
- **WHEN** all range values are `0.0`
- **THEN** vehicle count is `0` and closest distance is `null`

### Requirement: Threat level mapping is unit tested
The project SHALL have unit tests verifying the conversion from raw ANT+ threat integers to `ThreatLevel` enum values.

#### Scenario: Valid and invalid threat values
- **WHEN** threat values `0`, `1`, `2`, `3`, and an out-of-range value are converted
- **THEN** they map to `CLEAR`, `APPROACHING`, `WARNING`, `CRITICAL`, and default to `CLEAR` respectively

### Requirement: Relative speed derivation is unit tested
The project SHALL have unit tests covering the derived relative speed algorithm, including constant approach, handoff suppression, negative clamping, and maximum cap.

#### Scenario: Vehicle approaching at constant speed
- **WHEN** distance decreases by `30` meters over `3` seconds
- **THEN** the derived relative speed converges near `36` km/h

#### Scenario: Handoff suppresses speed display
- **WHEN** the closest target slot changes
- **THEN** relative speed returns `null` for exactly `2` ticks before resuming

#### Scenario: Negative computed speed is clamped
- **WHEN** distance increases between samples
- **THEN** the derived relative speed is clamped to `0`

### Requirement: Absolute speed computation is unit tested
The project SHALL have unit tests verifying that absolute speed equals relative speed plus cyclist speed, and returns `null` when either input is unavailable.

#### Scenario: Both speeds available
- **WHEN** relative speed is `20` km/h and cyclist speed is `30` km/h
- **THEN** absolute speed is `50` km/h

#### Scenario: Cyclist speed unavailable
- **WHEN** relative speed is available but cyclist speed is `null`
- **THEN** absolute speed is `null`

### Requirement: FIT recording gating is unit tested
The project SHALL have unit tests verifying that `FitRecorder` emits developer fields only while recording and connected, and skips emission when paused or disconnected.

#### Scenario: Recording with radar connected
- **WHEN** ride state is `Recording` and radar is connected
- **THEN** a `WriteToRecordMesg` is emitted with all four developer fields

#### Scenario: Paused ride
- **WHEN** ride state transitions to `Paused`
- **THEN** no further `WriteToRecordMesg` is emitted

#### Scenario: Radar disconnected
- **WHEN** radar disconnects during an active recording
- **THEN** `WriteToRecordMesg` is skipped until the radar reconnects

### Requirement: Helper conversions are unit tested
The project SHALL have unit tests for `Units.metersToFeet`, `Units.kmhToMph`, and `ThreatColors.toBackgroundColor`.

#### Scenario: Imperial conversions
- **WHEN** `1.0` meter and `1.0` km/h are converted to imperial
- **THEN** the results match the defined conversion constants

#### Scenario: Threat colors
- **WHEN** each `ThreatLevel` is converted to a background color
- **THEN** `CLEAR` is transparent and the other levels return their expected colors
