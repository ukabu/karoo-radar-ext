## Why

The new RemoteViews-based datafields have two visual problems on Karoo 3:

1. **Background color does not cover the title/header.** We currently rely on `UpdateGraphicConfig(showHeader = true)` so Karoo draws the header natively. But Karoo reserves a header strip outside our RemoteViews, so our threat-colored background only fills the remaining body area. Native Karoo fields (e.g., HR Zone, 10s Power) color the entire cell including the header.

2. **The value area looks squished.** Because Karoo reserves header space outside our RemoteViews, the available area for the value is smaller than native fields get. Our value looks vertically compressed and doesn't have enough breathing room.

To fix both issues we need to draw the header ourselves inside the RemoteViews and use a layout that mirrors native Karoo anatomy (header reference + weighted value box), as demonstrated by the Barberfish extension.

## What Changes

- Switch back to `UpdateGraphicConfig(showHeader = false)` so our RemoteViews controls the entire cell.
- Re-add a label `TextView` to the datafield layout and use the actual Karoo system fonts:
  - Label: **Ping LCG** (medium or regular), all-caps.
  - Value: **Hammerhead Relative Mono 12**.
- Implement a Barberfish-style layout with an invisible header-reference probe and a weighted value box, so the value is centered in the space below the header and the background covers the entire cell.
- Add text-color handling for both label and value on colored backgrounds (black on approaching/warning/critical; theme-aware on clear).
- Remove the unused `labelRes` handling in `DatafieldUtils` or keep it for the XML label text.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `datafield-display`: datafields SHALL render the full cell (header + value) inside the extension's RemoteViews, use native Karoo fonts, and color the entire cell by threat level.

## Impact

- `app/src/main/kotlin/com/ukabu/karooradar/DatafieldUtils.kt`: change `showHeader` to `false`, pass label to RemoteViews helper.
- `app/src/main/kotlin/com/ukabu/karooradar/RadarDatafieldRemoteViews.kt`: set label text and colors, remove `showHeader` assumption.
- `app/src/main/res/layout/radar_datafield*.xml`: add header label, header-reference probe, weighted value box.
- `app/src/main/res/values/strings.xml`: may shorten or adjust labels to fit header height.
