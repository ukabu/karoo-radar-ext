## Why

Our custom radar datafields currently render with Glance and do not visually match native Karoo fields. Differences include: no rounded corners, wrong font and sizing, label/value centered vertically instead of a top-aligned header, units displayed in the value string, and threat colors that do not match Karoo's power-field palette. Switching to XML RemoteViews will give us the layout precision needed to align with Karoo's design language.

## What Changes

- Replace the Glance-based datafield renderer with XML `RemoteViews` layouts.
- Add layout XML for right, left, and center alignment matching `ViewConfig.Alignment`.
- Let Karoo render the header label natively and remove units from the value display.
- Use Karoo-aligned threat colors (yellow, orange, red) matching native power fields.
- Support day/night default text colors.
- Shorten header labels to fit on a single line by removing the "Radar" prefix.
- Apply rounded corners for the config preview and keep transparent/rounded behavior consistent with native cells.
- Remove or reduce the Glance dependency.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `datafield-display`: datafields SHALL render via XML RemoteViews and visually align with native Karoo fields.

## Impact

- `app/src/main/kotlin/com/ukabu/karooradar/RadarDatafieldGlance.kt`: deleted.
- `app/src/main/kotlin/com/ukabu/karooradar/DatafieldUtils.kt`: updated to use RemoteViews.
- `app/src/main/kotlin/com/ukabu/karooradar/ThreatColors.kt`: updated color values and text-contrast logic.
- `app/src/main/res/layout/`: new datafield layouts.
- `app/src/main/res/drawable/`: new background shape drawable.
- `app/src/main/res/values/colors.xml`: updated threat colors.
- `app/src/main/res/values/strings.xml`: shortened display labels.
- `app/build.gradle.kts`: remove Glance dependency.
