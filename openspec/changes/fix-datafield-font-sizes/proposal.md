## Why

After aligning the radar datafields with native Karoo layout and fonts, testing on a real Karoo 3 showed that:

1. The value font was too small. The numeric value "38" in ABS SPEED appeared tiny and left a lot of empty vertical space, while native fields like 10S POWER (221), AVG SPEED (19.2), DISTANCE (0.82), and CADENCE (90) have values that fill most of the available vertical space below the header.
2. The header label was too small and sat too close to the top/right edges compared to native headers.

## What Changes

- Pass `ViewConfig.textSize` from the Karoo SDK into the RemoteViews helper.
- Set the value `TextView` text size to `config.textSize` so it scales with the cell.
- Set the header label to 17sp bold using the native **Ping L** font.
- Add 7dp top padding and 3dp end padding to match native header placement.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `datafield-display`: value font size follows `ViewConfig.textSize`; header label uses native font, size, and padding.

## Impact

- `app/src/main/kotlin/com/ukabu/karooradar/DatafieldUtils.kt`: pass `config.textSize` to RemoteViews helper.
- `app/src/main/kotlin/com/ukabu/karooradar/RadarDatafieldRemoteViews.kt`: use `textSize` to size value; set label text and color.
- `app/src/main/res/layout/radar_datafield*.xml`: set label font, size, padding, and alignment.
