## ADDED Requirements

### Requirement: Four selectable datafield types registered
The extension SHALL register four distinct `DataType` entries with the Karoo system so users can add them to ride profiles: Vehicle Count, Closest Vehicle Distance, Closest Vehicle Relative Speed, Closest Vehicle Absolute Speed.

#### Scenario: User browses datafields
- **WHEN** a user opens the ride profile editor and browses available datafields
- **THEN** all four radar datafield types appear in the list under the extension's category

### Requirement: Each datafield renders with Glance
Each datafield SHALL render using a single size-agnostic Glance composable with a small localized label and a large centered numeric value.

#### Scenario: Datafield added to profile
- **WHEN** a user adds a radar datafield to their ride profile and starts a ride
- **THEN** the datafield displays a localized label above or below the numeric value

### Requirement: Display units follow Karoo system preference
All datafields SHALL display values using the Karoo system unit preference (metric or imperial). Distances show as whole numbers in the selected unit; speeds show as whole numbers in the selected unit.

#### Scenario: Karoo set to imperial
- **WHEN** the Karoo system preference is set to imperial
- **THEN** distance datafields display in feet, speed datafields display in mph, both as whole numbers

### Requirement: Background color driven by threat level
The background color of each datafield SHALL change based on the current radar threat level:
- CLEAR (0): neutral/default background
- APPROACHING (1): yellow
- WARNING (2): orange
- CRITICAL (3): red

#### Scenario: Vehicle approaches in WARNING zone
- **WHEN** the radar reports threat level WARNING
- **THEN** all four datafield backgrounds change to orange simultaneously

### Requirement: No-vehicle display state
When zero vehicles are detected, Vehicle Count SHALL display `0`; distance and speed datafields SHALL display `--` (or empty), and background SHALL be neutral.

#### Scenario: Radar detects no vehicles
- **WHEN** the radar stream reports all target ranges as zero
- **THEN** count shows `0`, distance/speed show `--`, and background is neutral

### Requirement: Radar disconnected display state
When the radar is disconnected, all datafields SHALL display `--` with a neutral background.

#### Scenario: Radar disconnects mid-ride
- **WHEN** the radar ANT+ connection is lost
- **THEN** all datafields show `--` and the background returns to neutral
