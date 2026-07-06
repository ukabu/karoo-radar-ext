## MODIFIED Requirements

### Requirement: Four selectable datafield types registered
The extension SHALL register four distinct `DataType` entries with the Karoo system so users can add them to ride profiles: Vehicle Count, Closest Vehicle Distance, Closest Vehicle Relative Speed, Closest Vehicle Absolute Speed.

#### Scenario: User browses datafields
- **WHEN** a user opens the ride profile editor and browses available datafields
- **THEN** all four radar datafield types appear in the list under the extension's category

### Requirement: Each datafield renders with native-style layout
Each datafield SHALL render using XML RemoteViews with a centered numeric value and a transparent or colored background. The label header is rendered by the Karoo system using native fonts and styling so it matches other native datafields.

#### Scenario: Datafield added to profile
- **WHEN** a user adds a radar datafield to their ride profile and starts a ride
- **THEN** the Karoo-rendered label appears at the top and the RemoteViews value is centered in the remaining area

### Requirement: Display units follow Karoo system preference
All datafields SHALL display values using the Karoo system unit preference (metric or imperial). Distances show as whole numbers; speeds show as whole numbers. The displayed value SHALL NOT include a unit suffix.

#### Scenario: Karoo set to imperial
- **WHEN** the Karoo system preference is set to imperial and a ride is active
- **THEN** distance datafields display in feet, speed datafields display in mph, both as whole numbers without units

### Requirement: Background color driven by threat level
The background color of each datafield SHALL change based on the current radar threat level:
- CLEAR (0): transparent/default background
- APPROACHING (1): Karoo yellow
- WARNING (2): Karoo orange
- CRITICAL (3): Karoo red

#### Scenario: Vehicle approaches in WARNING zone
- **WHEN** the radar reports threat level WARNING
- **THEN** all four datafield backgrounds change to Karoo orange simultaneously

### Requirement: No-vehicle display state
When zero vehicles are detected, Vehicle Count SHALL display `0`; distance and speed datafields SHALL display `--`, and background SHALL be neutral/transparent.

#### Scenario: Radar detects no vehicles
- **WHEN** the radar stream reports all target ranges as zero
- **THEN** count shows `0`, distance/speed show `--`, and background is neutral

### Requirement: Radar disconnected display state
When the radar is disconnected, all datafields SHALL display `--` with a neutral/transparent background.

#### Scenario: Radar disconnects mid-ride
- **WHEN** the radar ANT+ connection is lost
- **THEN** all datafields show `--` and the background returns to neutral

### Requirement: Day/night text color awareness
Text on transparent backgrounds SHALL follow the Karoo day/night theme: white in night mode, black in day mode. Text on colored threat backgrounds SHALL be black.

#### Scenario: Night ride with no vehicles
- **WHEN** the Karoo is in night mode and no vehicles are detected
- **THEN** datafield text is white on the default dark cell background

### Requirement: Display names fit on a single line
Each datafield's display name SHALL be short enough to render on a single line in the Karoo header. The "Radar" prefix is omitted from the header labels because the extension icon already identifies the data source.

#### Scenario: Field added to a narrow profile slot
- **WHEN** a datafield is added to a narrow column in a ride profile
- **THEN** its header label renders as a single line (e.g., "Vehicles", "Distance", "Rel Speed", "Abs Speed")

### Requirement: Layout respects ViewConfig.Alignment
The datafield value layout SHALL adapt to the alignment provided by `ViewConfig.Alignment` (LEFT, CENTER, RIGHT).

#### Scenario: Field aligned to the left side of the profile
- **WHEN** a datafield is placed in a left-aligned slot
- **THEN** the value is laid out for left alignment
