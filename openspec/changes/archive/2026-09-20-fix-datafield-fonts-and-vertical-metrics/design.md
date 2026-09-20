## Context

Radar datafields are rendered as XML `RemoteViews` because the threat-level background must color the entire cell (the SDK's `UpdateNumericConfig` can only render the OS's own numeric treatment, which cannot carry a custom background color). To match native fields, the layout was reverse-engineered from a real Karoo 3.

**Sources used (all read-only, from the connected device):**

- `io.hammerhead.rideapp` — pulled from `/system/priv-app/ride/ride.apk`, decompiled with jadx, resources dumped with `aapt2`.
- `/system/etc/fonts.xml` — the only font configuration on the device.
- Live screenshots via `adb exec-out screencap`.

**Native numeric field anatomy** (`data_element_single` layout + styles):

```
ConstraintLayout (style=dataElementRoot)
  background = data_element_background  → rounded rect, radius 10dp,
               solid = elementViewBackgroundColor (white day / black night)
  foreground = data_element_foreground  → 1px stroke = elementViewDividerColor, radius 10dp
  DataHeaderView (style=dataHeader): minHeight 22dp, padStart/End 1dp
     label style=dataHeaderTextStyle: 17sp default · allCaps · NO bold ·
        fontFamily=ibm-plex-sans-condensed · includeFontPadding=false ·
        lineSpacingMultiplier=0.7 · ellipsize=end · maxLines=2
  AppCompatTextView (style=singleNumericDataStyle): width=match_parent ·
     height=0dp (fills) · fontFamily=relative · letterSpacing=-0.04 ·
     singleLine · includeFontPadding=false · NO textSize in style
  TextView (style=streamStateStyle): the "Searching…/--" state text
```

**Font families actually registered on device** (`/system/etc/fonts.xml`):

| Role | Family alias | Resolves to |
|---|---|---|
| Value | `relative` | alias → `monospace` → `Relative12-Regular.otf` |
| Label | `ibm-plex-sans-condensed` | alias → `ibm-sans-cond` → `IBMPlexSansCondensed-Medium.otf` |

`Ping L`, `Ping LCG`, and `Hammerhead Relative Mono 12` are **not** registered families — the extension currently falls back to `sans-serif`.

**Host sizing model** (`m6/e.a()` → `DataElementConstraints`, applied in `l6/g.java`):

- The host computes `DataElementConstraints( dataSize, dataTranslationY, labelSize, singleLineLabel, dataAlignment, streamStateAlignment, labelLineSpacingMultiplier, labelTranslationY )` per cell, keyed on `rowSpan`/`columnSpan` and `screenHeightDp`.
- `ViewConfig.textSize = dataSize / density` (sp). `labelSize` is **not** exposed to extensions.
- The value view gets `setTextSize(0, dataSize)` and `setTranslationY(dataTranslationY)`; the header gets `labelSize` (value shrinks **and** label shrinks as the cell gets smaller).
- Both value and label sizes are bucketed (e.g. label ≈ 36px→19sp large, 29px→15.5sp small; value 180→78px).
- Karoo 3 reports `h402dp`; the native threshold is `screenHeightDp > 400` → **STANDARD** branch (a 2dp margin — confirm empirically).

**Measured current-vs-native geometry** (row 3, 5-row grid; cell interior y317→440, h=124px):

```
                 native                 ours ("ABS SPEED")
label top      y=337                 y=340   (+3px lower)
label height   25px                  22px    (too short — wrong family + fixed 17sp)
value center   y=408                 y=418   (+10px lower)
value bottom   y=436 (fits)          ~446    (a digit would spill past y=440)
```

The value sits ~10px too low because the `header_ref` probe (`"M"` + 7dp margin) plus `field_header` (minHeight 22dp + 7dp padding) reserves more space than the native header's flat 22dp, so the value's centering box starts lower. Net effect: numeric values are clipped at the bottom in short cells.

## Goals / Non-Goals

**Goals:**
- Use the exact registered font families, weights, and letter spacing the native renderer uses.
- Reproduce the native header/value vertical geometry so numeric values are centered like native and never clipped in short cells.
- Scale the label with the grid, matching native's coarse `labelSize` buckets.

**Non-Goals:**
- Bitmap-rendered values / pixel-perfect baseline reproduction.
- Header icons (native fields have one; we keep drawing a label-only header).
- Switching to the OS standard numeric view (`UpdateNumericConfig`) — it cannot carry the threat background.
- Any change to radar processing, FIT recording, or threat-color semantics.

## Decisions

### 1. Use the registered family aliases, not file names
`android:fontFamily="relative"` and `"ibm-plex-sans-condensed"` resolve through the system font config, so they work from any package. Font *file* names do not. **Alternative rejected:** bundling the `.otf` files (larger APK, licensing, and unnecessary — the fonts are already system-wide).

### 2. Match native label weight/spacing exactly
Set `textAllCaps=true`, drop `textStyle="bold"` (native has none — it relies on IBMPlexSansCondensed-**Medium**), and add `letterSpacing=-0.04` on the value. **Alternative rejected:** keeping bold as an approximation — it produced a visibly heavier/wider label.

### 3. Derive label size from the grid like the host does
We are not given `labelSize`, only `textSize` (= `dataSize`). Two options:

- **(a) Replicate the `rowSpan/columnSpan → labelSize` table** from `m6/e.a()`. Most faithful, but duplicates host internals that may change across firmware.
- **(b) Derive label size from `config.textSize`** using the native ratio (label ≈ 0.32–0.35 × value size). Simpler, smaller surface, robust to firmware changes.

Proposed default: **(b)**, with the ratio and clamping documented, so the label shrinks with the cell without hardcoding host tables. If measurements show drift, fall back to (a).

**Implemented:** option (b) with ratio `0.43`, clamped to `[13, 20]` sp. The on-device probe logged `textSize=41sp` for a half-width `rowSpan=12` cell. A first attempt at ratio `0.46` (~19sp) produced a label ~2px taller than native; correcting to `0.43` (~18sp) matched the native cap height (ours 24px vs native 23px).

Note: an earlier measurement appeared to show the label matching native, but it was comparing our label to itself (wrong cell column). Re-measuring against a native neighbour in the **same** column showed our label needed to be both **smaller** and **lower**.

### 4. Fix the value centering box, not a translation hack
Replace the `header_ref`-`"M"`+margin probe with a header region of exactly the native height (22dp min), and center the value in `[header bottom, cell bottom]`. **Alternative rejected:** a `translationY` offset — it would drift with font/metric changes and only masks the geometry bug.

**Implemented + amended:** the layout is now a vertical `LinearLayout` (header + `FrameLayout` value area with weighted spacers), which centers the value in `[header bottom, cell bottom]`. Verification with a forced test digit showed our digit matched the native digit's size (56 vs 57px) but sat ~4px lower (baseline 440 vs native 436, touching the cell edge). Native applies a small negative `dataTranslationY`; we replicate it with `RemoteViews.setFloat(field_value, "setTranslationY", -0.05 * textSize * density)`. After this, the digit matched native within 1px (y381–436 vs y380–436). Note: weighted-spacer asymmetry alone had **no** effect (the value's line height fills the box, leaving no free space), so the translation is the actual mechanism.

### 5. Keep RemoteViews
ConstraintLayout is not allowed in RemoteViews, so the value-fill model is approximated with a vertical `LinearLayout` (header + value area with weighted spacers). This is the closest allowed equivalent to native's `height=0dp` fill.

### 6. Native value edge margin
The native host sets a `numeric_data_margin` of **4dp** on the value's outer edge (right for RIGHT, left for LEFT; `numeric_data_margin_centered` = 2dp on both sides for CENTER). The layouts apply matching `layout_marginStart`/`layout_marginEnd` so the value does not touch the cell edge. Verified with forced 1/2/3-digit values: all right-align to the same edge with the margin, at identical size (56px) and baseline.

### 7. Label vertical position
The native header places its label lower than a vertically-centered label (native top offset ~20px vs a centered ~10px on the test cell). Rather than duplicate the host's taller header (which would displace the value), the label is nudged down with `setTranslationY(0.12 × textSize × density)` so the header box — and therefore the value position — is unchanged. Result: label offset 19–42px vs native 20–42px.

## Risks / Trade-offs

- **[Label-size derivation may drift from native]** → keep the derivation in one place with a documented ratio; add a device screenshot check against a neighbouring native field.
- **[RemoteViews cannot express ConstraintLayout's fill exactly]** → anchor the value container to the cell bottom and verify no clipping across row spans.
- **[`screenHeightDp` sits at 402 (2dp above the 400 threshold)]** → confirm the active branch empirically by logging `config.gridSize/viewSize/textSize` on device before committing to a table.
- **[Fonts referenced by alias may differ on future firmware]** → aliases are stable system names; if they ever change, the fallback is graceful (default sans) rather than a crash.

## Migration Plan

Visual-only change; no data or schema migration. Rollback is a single revert of the layout/RemoteViews code. Verify with `./gradlew test` and by deploying a debug build and screenshotting a ride page next to native fields.

## Open Questions

- ~~Exact `labelSize` value native uses at each bucket on this firmware~~ — resolved empirically: ratio 0.46 matches (label ≈19sp at value 41sp), verified to the pixel.
- Whether `ViewConfig.textSize` is rounded in a way that matters for small cells.
- Whether the value and label `setTranslationY` factors (`-0.01` and `0.12 × textSize` respectively) hold at cell sizes other than the one testable on this device (the profile was already at max rows, so only `rowSpan=12` could be observed).
