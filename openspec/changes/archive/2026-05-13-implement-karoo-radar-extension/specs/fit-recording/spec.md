## ADDED Requirements

### Requirement: Developer field schema defined
The extension SHALL define four scalar developer fields with the following types:
- `radar_vehicles` (uint8) — number of detected vehicles
- `radar_nearest_distance_m` (uint16) — closest vehicle distance in meters
- `radar_relative_speed_kmh` (uint8) — derived closest-vehicle relative speed in km/h
- `radar_absolute_speed_kmh` (uint8) — derived closest-vehicle absolute speed in km/h

#### Scenario: FIT file recorded
- **WHEN** a ride is recorded with radar data present
- **THEN** the FIT file contains the four developer fields under the extension's developer data index

### Requirement: Developer fields stored in SI units
All developer field values SHALL be stored in SI units regardless of Karoo display unit preference: distances in meters, speeds in km/h.

#### Scenario: Karoo set to imperial
- **WHEN** the Karoo is set to imperial and a ride is recorded
- **THEN** FIT values are still stored in meters and km/h

### Requirement: FIT emission gated by RideState
Developer fields SHALL be written to the FIT record message only when `RideState.Recording` is active. When the ride is paused (manual or auto-pause), writing SHALL cease.

#### Scenario: Rider pauses ride
- **WHEN** the ride transitions from Recording to Paused
- **THEN** developer fields stop being written until Recording resumes

### Requirement: Radar disconnect skips FIT write
When the radar is disconnected during an active ride, the extension SHALL skip writing developer fields for that record cycle. Writing SHALL resume when the radar reconnects.

#### Scenario: Radar disconnects mid-ride
- **WHEN** the radar disconnects while Recording is active
- **THEN** no radar developer fields are written for the duration of the disconnect

### Requirement: Emission rate at approximately 1 Hz
Developer fields SHALL be written at approximately once per second while all gating conditions are met.

#### Scenario: Normal active recording
- **WHEN** the ride is Recording and radar is connected
- **THEN** each FIT record contains one set of radar developer fields
