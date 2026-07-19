## Context

The previous `align-datafields-with-karoo-design` change replaced Glance with XML RemoteViews and then, to match native header fonts, opted to let Karoo draw the header itself via `showHeader = true`. A photo from a real Karoo 3 ride shows this creates two problems:

1. The threat-colored background only fills the body below Karoo's header, leaving the header uncolored.
2. The value area is smaller than native fields because Karoo reserves space for the external header, making the field look squished.

Native Karoo fields color the entire cell (header + value) when their metric is in a zone (e.g., HR Zone, 10s Power). The correct fix is to draw the header inside our RemoteViews so we control the full cell, and to use the actual Karoo system fonts that are present on the device.

## Goals / Non-Goals

**Goals:**
- Render the full datafield cell (header + value) inside the extension's RemoteViews.
- Use the actual Karoo system fonts: **Ping LCG** for the label and **Hammerhead Relative Mono 12** for the value.
- Ensure the threat-colored background covers the entire cell.
- Match native field vertical proportions by reserving header space and centering the value in the remaining area.
- Support LEFT, CENTER, and RIGHT alignment via separate layouts.

**Non-Goals:**
- Bitmap-rendered values with pixel-perfect baseline alignment (Barberfish goes this far; we will keep the value as a TextView for simplicity but may revisit if needed).
- Dynamic per-cell font shrinking based on content width for the first pass.
- Icons in the header for the first pass.

## Decisions

### 1. Use `showHeader = false`
Letting Karoo draw the header gave us the right font but the wrong layout. We will draw the header ourselves so the background drawable and layout extend edge-to-edge.

### 2. Use native Karoo fonts discovered on device
ADB inspection of `/system/fonts/` confirmed the Figma fonts are installed:
- Label: `Ping LCG` (`Ping-LCG-Regular.otf`, `Ping-LCG-Medium.otf`)
- Value: `Hammerhead Relative Mono 12` (`Relative12-Regular.otf`)

This is better than bundling a font or using generic Android sans-serif.

### 3. Adopt Barberfish layout anatomy
Barberfish uses a stable layout structure that mirrors native Karoo fields:
- An invisible `header_ref` `TextView` at the top to reserve exact header height.
- A visible `field_header` `LinearLayout` at the top containing the label.
- A `baseline_box` `LinearLayout` below `header_ref` with weighted spacers to vertically center the value in the remaining space.

For our first pass we will keep the value as a `TextView` rather than Barberfish's bitmap `ImageView`, because that is simpler and probably good enough. If the value baseline still doesn't align nicely with native fields, we can switch to bitmap rendering in a follow-up.

### 4. Background drawable covers full root
The colored rounded-corner shape drawable will be set on the root `RelativeLayout` so it fills the entire cell, header included.

### 5. Text color contrast
- On CLEAR/transparent background: label and value follow day/night theme (white night, black day).
- On APPROACHING/WARNING/CRITICAL backgrounds: label and value use black for readability, matching native zone fields.

### 6. Keep alignment variants
We will maintain three layouts for right, left, and center alignment. The only differences will be label/value gravity and alignments.

### 7. Header label text
We will keep the shortened labels currently in `strings.xml`: "Vehicles", "Distance", "Rel Speed", "Abs Speed". They render all-caps via `textAllCaps="true"`.

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| RemoteViews may not load custom/system font families by file name | Test on device; if `fontFamily="Ping LCG"` fails, fall back to `"Ping-LCG-Regular"` or use IBM Plex Sans Condensed. |
| TextView value may not baseline-align as precisely as native fields | First pass uses TextView; if needed, follow-up switches to bitmap `ImageView` like Barberfish. |
| Header height may differ from native across Karoo firmware versions | Use wrap_content on the label plus a minimum header height; observe on device. |

## Migration Plan

Not applicable. This is a visual fix to the existing RemoteViews implementation.

## Open Questions

None.
