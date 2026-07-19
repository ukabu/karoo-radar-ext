## MODIFIED Requirements

### Requirement: Full-cell rendering
Each datafield SHALL render its header label and numeric value inside the extension's own RemoteViews, using `UpdateGraphicConfig(showHeader = false)`, so the extension controls the entire cell rectangle.

#### Scenario: User adds a radar datafield to a profile
- **WHEN** a radar datafield is displayed in a ride profile
- **THEN** the extension's RemoteViews fills the entire datafield cell, including the area normally occupied by the header

### Requirement: Native Karoo fonts
The datafield label SHALL use the Karoo system font **Ping LCG** and the value SHALL use the Karoo system font **Hammerhead Relative Mono 12**, both discovered on device at `/system/fonts/`.

#### Scenario: Datafield displayed during a ride
- **WHEN** a radar datafield is visible
- **THEN** its header label uses the Ping LCG typeface and its value uses the Hammerhead Relative Mono 12 typeface

### Requirement: Full-cell threat background
The colored threat background SHALL cover the entire datafield cell, including the header label area, not just the value area.

#### Scenario: Radar reports a WARNING threat
- **WHEN** the threat level changes to WARNING
- **THEN** the entire datafield cell (header and value) changes to the Karoo orange background simultaneously

### Requirement: Header and value vertical layout
The layout SHALL reserve a header strip at the top of the cell and center the value vertically in the remaining space, matching native Karoo field anatomy.

#### Scenario: Datafield shown in a standard 2x1 profile slot
- **WHEN** the datafield is rendered
- **THEN** the label sits at the top of the cell and the value is vertically centered in the remaining area below it

### Requirement: Alignment-specific layouts
The layout SHALL adapt to `ViewConfig.Alignment` (LEFT, CENTER, RIGHT) using separate layout files so the label/value gravities match the slot alignment.

#### Scenario: Datafield in a left-aligned slot
- **WHEN** a datafield is placed in a left-aligned slot
- **THEN** label and value are left-aligned within the cell

### Requirement: Text contrast on colored backgrounds
On CLEAR/transparent backgrounds, text SHALL follow the Karoo day/night theme. On APPROACHING/WARNING/CRITICAL backgrounds, both label and value SHALL use black text.

#### Scenario: Vehicle approaches in APPROACHING zone
- **WHEN** the threat level is APPROACHING
- **THEN** the entire cell is yellow and both label and value text are black

### Requirement: No unit suffix in value
The displayed numeric value SHALL NOT include a unit suffix. Imperial/metric conversion still applies to the number.

#### Scenario: User preference is imperial
- **WHEN** system units are imperial
- **THEN** the value is displayed as a whole number without "ft" or "mph"

### Requirement: Radar state display states
When no vehicles are detected, count displays `0` and distance/speed display `--`. When radar is disconnected, all fields display `--`. Background is neutral in both cases.

#### Scenario: No vehicles detected
- **WHEN** the radar reports zero targets
- **THEN** count shows `0`, distance/speed show `--`, background is neutral
