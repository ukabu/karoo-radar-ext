## Context

The current datafield renderer uses Glance, which abstracts layout but lacks the precision needed to match native Karoo fields. Barberfish (https://github.com/jpweytjens/barberfish) demonstrates that XML `RemoteViews` with careful layout, dynamic font sizing, and day/night color handling can closely mirror native Karoo datafields.

## Goals / Non-Goals

**Goals:**
- Render radar datafields with XML RemoteViews so layout, fonts, and colors can match native Karoo fields.
- Move the label into a top header row; display only the numeric value (no unit suffix).
- Align value and label based on `ViewConfig.Alignment`.
- Use Karoo-aligned threat colors.
- Support day/night default text colors.

**Non-Goals:**
- Pixel-perfect duplication of Barberfish's bitmap-based value rendering.
- Dynamic per-cell font measurement down to the pixel.
- Icons in the header for the first pass.
- Changes to radar data processing or FIT recording.

## Decisions

### 1. Replace Glance with XML RemoteViews
Glance cannot easily produce the native header/value layout, rounded preview corners, or alignment-specific positioning. XML RemoteViews provide direct control. This adds a small amount of boilerplate but is the only path to visual alignment.

### 2. One layout with alignment variants
We will create three layout files (`radar_datafield.xml`, `radar_datafield_left.xml`, `radar_datafield_center.xml`) derived from Barberfish's pattern. Each shares the same structure but anchors the value differently.

### 3. Label drawn by Karoo, value centered below
Native Karoo fields keep the label in a header strip at the top and center the value in the remaining space. Rather than reimplement the header in RemoteViews (which cannot match Karoo's custom fonts), we configure `showHeader = true` and let Karoo draw the label natively. Our RemoteViews only renders the centered value and the threat-colored background.

### 4. No units in value
Native Karoo fields do not append units to the numeric value. Unit awareness is still used for conversion, but the displayed string will be just the number, `--`, or `0`.

### 5. Threat colors from Karoo palette
Based on Karoo power-field colors and Barberfish references:
- CLEAR: transparent background, default text color
- APPROACHING: `#FFFBE401` (Karoo yellow)
- WARNING: `#FFF57C00` (Karoo orange)
- CRITICAL: `#FFF44336` (Karoo red)
Text on colored backgrounds will be black for readability.

### 6. Day/night text color
When the threat level is CLEAR, text follows the Karoo theme: white in night mode, black in day mode. Colored backgrounds always use black text.

### 7. Short header labels
Display names are shortened to fit on a single line in narrow datafield slots. The "Radar" prefix is removed because the extension icon already identifies the data source:
- "Radar Vehicles" → "Vehicles"
- "Radar Distance" → "Distance"
- "Radar Rel Speed" → "Rel Speed"
- "Radar Abs Speed" → "Abs Speed"

### 8. Rounded corners for preview only
Barberfish rounds corners only in the config preview (`preview == true`). We will do the same, because Karoo itself rounds cells in the ride profile.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| RemoteViews are more verbose than Glance | Keep layouts simple; reuse one helper function to bind data. |
| Font sizing may not be perfect across all cell sizes | Use a few fixed size buckets based on cell width rather than full dynamic measurement. |
| Day/night detection adds complexity | Read `Configuration.UI_MODE_NIGHT_MASK` from `context.resources.configuration`. |

## Migration Plan

Not applicable. This is a visual refactor of an existing component.

## Open Questions

None.
