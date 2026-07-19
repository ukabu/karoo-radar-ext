## MODIFIED Requirements

### Requirement: Value font size follows ViewConfig.textSize
The numeric value in each datafield SHALL use the `textSize` provided by the Karoo SDK's `ViewConfig`, so the value scales with the cell size and fills the available vertical space like native fields.

#### Scenario: Datafield is shown in a standard 2x1 profile slot
- **WHEN** a radar datafield is rendered
- **THEN** the value text size matches the SDK-recommended size for that cell

### Requirement: Header label size matches native fields
The header label SHALL use a fixed text size of 17sp in bold, using the native **Ping L** typeface, so it matches the scale and weight of native field headers.

#### Scenario: Datafield header is visible
- **WHEN** the datafield label is shown
- **THEN** it is rendered at 17sp bold using Ping L

### Requirement: Header label padding matches native placement
The header label area SHALL have 7dp top padding and 3dp end padding to match the placement of native field headers.

#### Scenario: Datafield shown in a standard 2x1 profile slot
- **WHEN** the datafield is rendered
- **THEN** the header is offset from the top and right edges similarly to native fields

### Requirement: Existing visual behavior preserved
The fix SHALL not change fonts, layout structure, full-cell backgrounds, alignment handling, or threat color behavior.

#### Scenario: Radar threat level changes
- **WHEN** the threat level changes
- **THEN** the full cell still colors correctly and text contrast remains correct
