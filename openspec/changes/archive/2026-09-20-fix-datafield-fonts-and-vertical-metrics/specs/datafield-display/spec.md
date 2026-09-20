## ADDED Requirements

### Requirement: Four selectable datafield types registered
The extension SHALL register four distinct `DataType` entries with the Karoo system so users can add them to ride profiles: Vehicle Count, Closest Vehicle Distance, Closest Vehicle Relative Speed, Closest Vehicle Absolute Speed.

#### Scenario: User browses datafields
- **WHEN** a user opens the ride profile editor and browses available datafields
- **THEN** all four radar datafield types appear in the list under the extension's category

### Requirement: Full-cell rendering
Each datafield SHALL render its header label and numeric value inside the extension's own RemoteViews, using `UpdateGraphicConfig(showHeader = false)`, so the extension controls the entire cell rectangle.

#### Scenario: User adds a radar datafield to a profile
- **WHEN** a radar datafield is displayed in a ride profile
- **THEN** the extension's RemoteViews fills the entire datafield cell, including the area normally occupied by the header

### Requirement: Native Karoo fonts
The datafield SHALL use the exact font families registered in the Karoo system font configuration (`/system/etc/fonts.xml`): the value SHALL use the `relative` family (which resolves to `monospace` → `Relative12-Regular.otf`) and the header label SHALL use the `ibm-plex-sans-condensed` family (which resolves to `ibm-sans-cond` → `IBMPlexSansCondensed-Medium.otf`). Font families SHALL be referenced by their registered alias, never by `.otf` file name.

#### Scenario: Datafield displayed during a ride
- **WHEN** a radar datafield is visible
- **THEN** its header label renders in the `ibm-plex-sans-condensed` (condensed, Medium-weight) typeface and its value renders in the `relative` (Relative12 monospace) typeface, matching a neighbouring native field

#### Scenario: Unknown font family
- **WHEN** a font family name that is not registered in the system font configuration is used
- **THEN** the field SHALL NOT rely on it (no `.otf`-named families, no `Ping L`/`Ping LCG`/`Hammerhead Relative Mono 12`)

### Requirement: Native label styling
The header label SHALL match native styling: `textAllCaps=true`, no synthetic bold, `includeFontPadding=false`, and a line spacing multiplier of 0.7 for multi-line labels. The label SHALL NOT use `textStyle="bold"`.

#### Scenario: Label rendered on a cell
- **WHEN** the header label is displayed
- **THEN** it is all-caps in the condensed Medium-weight font with no synthetic bold, matching the native header weight and width

### Requirement: Grid-aware label sizing
The header label size SHALL scale with the cell's grid size to mirror the native host's label behaviour, rather than using a single fixed size. It SHALL be derived from `ViewConfig.textSize` (native label ≈ 0.43 × value) and clamped so it stays legible in short cells and never exceeds a native header.

#### Scenario: Standard cell
- **WHEN** the datafield occupies a standard (tall) grid slot
- **THEN** the label size matches the native header size for that slot (within ~1px cap height)

#### Scenario: Short cell
- **WHEN** the datafield occupies a short grid slot
- **THEN** the label shrinks with the value and does not crowd out the value

### Requirement: Grid-aware label vertical position
The header label SHALL sit at the same vertical position as a native field's label of the same grid size, which is lower than a vertically-centered label, without changing the header height that positions the value.

#### Scenario: Label compared to a native field
- **WHEN** a radar datafield sits beside a native datafield of the same size
- **THEN** their header labels are vertically aligned (within ~1px) and the value baselines match

### Requirement: Header and value vertical layout
The layout SHALL reserve a header strip of the native header height (22dp minimum) at the top of the cell and vertically center the value in the remaining area `[header bottom, cell bottom]`, so the value's vertical center matches a native field of the same grid size and the value is not clipped when the cell is short.

#### Scenario: Datafield shown in a standard slot
- **WHEN** the datafield is rendered at a given grid size
- **THEN** the label sits in the top header strip and the value is vertically centered in the area below it

#### Scenario: Value in a short cell
- **WHEN** the cell is short enough that a native field would shrink its value
- **THEN** the full numeric value (or `--`) is rendered without being clipped by the bottom of the cell

### Requirement: Native value styling
The numeric value SHALL use `letterSpacing = -0.04`, `singleLine`, and `includeFontPadding = false`, and its text size SHALL come from `ViewConfig.textSize` (the host-provided value size for the current grid).

#### Scenario: Value rendered in a cell
- **WHEN** a numeric value is displayed
- **THEN** it uses the host-provided text size with the native letter spacing and no extra font padding

#### Scenario: Value edge margin
- **WHEN** a numeric value is displayed
- **THEN** it keeps a native-matching margin from the cell edge on its aligned side (4dp for LEFT/RIGHT, 2dp each side for CENTER) and does not touch the cell border

### Requirement: Alignment-specific layouts
The layout SHALL adapt to `ViewConfig.Alignment` (LEFT, CENTER, RIGHT) using separate layout files so the label and value gravities match the slot alignment.

#### Scenario: Datafield in a left-aligned slot
- **WHEN** a datafield is placed in a left-aligned slot
- **THEN** label and value are left-aligned within the cell

### Requirement: Full-cell threat background
The colored threat background SHALL cover the entire datafield cell, including the header label area, not just the value area.

#### Scenario: Radar reports a WARNING threat
- **WHEN** the threat level changes to WARNING
- **THEN** the entire datafield cell (header and value) changes to the Karoo orange background simultaneously

### Requirement: Background color driven by threat level
The background color of each datafield SHALL change based on the current radar threat level:
- CLEAR: transparent/default background
- APPROACHING: Karoo yellow
- WARNING: Karoo orange
- CRITICAL: Karoo red

#### Scenario: Vehicle approaches in WARNING zone
- **WHEN** the radar reports threat level WARNING
- **THEN** all four datafield backgrounds change to Karoo orange simultaneously

### Requirement: Text contrast on colored backgrounds
On CLEAR/transparent backgrounds, text SHALL follow the Karoo day/night theme (white in night mode, black in day mode). On APPROACHING/WARNING/CRITICAL backgrounds, both label and value SHALL use black text.

#### Scenario: Night ride with no vehicles
- **WHEN** the Karoo is in night mode and no vehicles are detected
- **THEN** datafield text is white on the default dark cell background

#### Scenario: Vehicle approaches in APPROACHING zone
- **WHEN** the threat level is APPROACHING
- **THEN** the entire cell is yellow and both label and value text are black

### Requirement: Units follow the Karoo system preference
All datafields SHALL display values using the Karoo system unit preference (metric or imperial), as whole numbers. The displayed numeric value SHALL NOT include a unit suffix.

#### Scenario: Karoo set to imperial
- **WHEN** the Karoo system preference is set to imperial and a ride is active
- **THEN** distance datafields display in feet and speed datafields in mph, both as whole numbers without units

### Requirement: Radar state display states
When no vehicles are detected, count SHALL display `0` and distance/speed SHALL display `--`. When the radar is disconnected, all fields SHALL display `--`. Background SHALL be neutral in both cases.

#### Scenario: No vehicles detected
- **WHEN** the radar reports zero targets
- **THEN** count shows `0`, distance/speed show `--`, background is neutral

#### Scenario: Radar disconnects mid-ride
- **WHEN** the radar ANT+ connection is lost
- **THEN** all datafields show `--` and the background returns to neutral

### Requirement: Display names fit on a single line
Each datafield's display name SHALL be short enough to render on a single line in the Karoo header. The "Radar" prefix is omitted because the extension icon already identifies the data source.

#### Scenario: Field added to a narrow profile slot
- **WHEN** a datafield is added to a narrow column in a ride profile
- **THEN** its header label renders as a single line (e.g., "Vehicles", "Distance", "Rel Speed", "Abs Speed")

### Requirement: Native source references recorded
The reverse-engineering sources used to derive the native field metrics SHALL be recorded in `docs/references.md` so the measurements can be re-verified against a future firmware.

#### Scenario: Future firmware change
- **WHEN** a maintainer needs to re-verify native metrics
- **THEN** `docs/references.md` identifies the ride app APK path, the font configuration path, and the decompiled classes/styles used
