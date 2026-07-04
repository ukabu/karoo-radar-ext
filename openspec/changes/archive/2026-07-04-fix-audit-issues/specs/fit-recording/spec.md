## ADDED Requirements

### Requirement: Radar Developer Field Schema
The extension SHALL define four scalar developer fields with the following types:
- `radar_vehicles` (uint8) — number of detected vehicles
- `radar_nearest_distance_m` (uint16) — closest vehicle distance in meters
- `radar_relative_speed_kmh` (uint8) — derived closest-vehicle relative speed in km/h
- `radar_absolute_speed_kmh` (uint8) — derived closest-vehicle absolute speed in km/h

#### Scenario: FIT file recorded with all radar data available
- **WHEN** a ride is recorded with radar connected and vehicles detected
- **THEN** the FIT file contains all four developer fields with non-zero values

#### Scenario: FIT file recorded with no vehicles
- **WHEN** a ride is recorded and the radar reports zero vehicles
- **THEN** all four developer fields SHALL still be present in the record with value `0`

#### Scenario: FIT file recorded during handoff
- **WHEN** a ride is recorded and the closest vehicle is in a handoff state
- **THEN** distance, relative speed, and absolute speed fields SHALL be present with value `0`, while `radar_vehicles` reflects the current count

#### Scenario: FIT file recorded during radar disconnect
- **WHEN** the radar disconnects during an active recording
- **THEN** the extension skips writing developer fields entirely for that record cycle
