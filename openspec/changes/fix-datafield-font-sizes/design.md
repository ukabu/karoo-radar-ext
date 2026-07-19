## Context

The Karoo SDK provides `ViewConfig.textSize`, which is the recommended value font size in sp for a given datafield cell. Native Karoo fields use this size to make the value fill the available vertical space below the header. Our current RemoteViews implementation did not set an explicit text size on the value, so it defaulted to a small size. The header label was also too small and lacked the right top/right spacing compared to native fields.

## Goals / Non-Goals

**Goals:**
- Use `ViewConfig.textSize` for the numeric value so it scales with the cell and fills vertical space.
- Increase header label size to 17sp bold with the native **Ping L** font.
- Add top and end padding to the header so it sits like native labels.
- Keep all existing behavior (full-cell backgrounds, native value font, alignment, threat colors).

**Non-Goals:**
- Dynamic text shrinking based on content width for this fix.
- Perfect width match of the label font; investigation shows native labels may use a slightly narrower variant, but this is left for a future iteration.

## Decisions

### 1. Value size from ViewConfig.textSize
`ViewConfig.textSize` is the SDK's signal for how large the value should be. We pass it through and apply it to `field_value` via `RemoteViews.setTextViewTextSize(..., COMPLEX_UNIT_SP, textSize)`.

### 2. Label font and size
- Font: native **Ping L** (system font on Karoo 3).
- Size: 17sp bold (`textStyle="bold"`).
- Padding: 7dp top, 3dp end, matching native header placement on the device.

### 3. No dynamic shrinking yet
If long values overflow after using `textSize`, we can add measurement-based shrinking later. Radar values are typically short (1-3 digits).

## Risks / Trade-offs

| Risk | Mitigation |
|---|---|
| `textSize` may be too large for very small cells | Tested on device with standard 2x1 cells; appears correct. |
| `Ping L` may still look slightly wider than native labels | Acceptable for now; can revisit with a narrower system font later. |
| Different alignments may need different treatment | All three layouts share the same value view structure, so one code path handles them. |

## Migration Plan

Not applicable.

## Open Questions

None.
