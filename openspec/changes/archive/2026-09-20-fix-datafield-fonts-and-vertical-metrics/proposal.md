## Why

The radar datafields render close to native Karoo fields but two measured defects remain on the Karoo 3:

1. **Wrong fonts.** The label uses `fontFamily="Ping L"` and the value uses `"Hammerhead Relative Mono 12"`. Neither family is registered on the device (`/system/etc/fonts.xml`), so both silently fall back to the default `sans-serif`. Result: our label is wider and thinner than native.
2. **Value clipped in short cells.** Our hand-rolled header probe reserves more vertical space than the native fixed 22dp header and centers the value ~10px too low. Extrapolated with a real number, the glyph passes the cell bottom — the "cut off when the field gets smaller" symptom.

Reverse-engineering the native renderer (`io.hammerhead.rideapp` APK + on-device `/system/etc/fonts.xml`) yielded the exact font families, label/value sizes, and vertical constraint model, so the fix is now well-defined rather than guessed.

## What Changes

- Use the **real system font families** discovered on device:
  - Value: `relative` (→ `monospace` → `Relative12-Regular.otf`)
  - Label: `ibm-plex-sans-condensed` (→ `IBMPlexSansCondensed-Medium.otf`)
- **Remove synthetic bold** from the label; use the Medium-weight condensed font with `textAllCaps` (native style has no bold).
- Apply the native value polish: `letterSpacing = -0.04`, `includeFontPadding = false`, `singleLine`.
- **Rebuild the header/value geometry** to mirror the native constraint model: the header reserves exactly the native header height (22dp min) and the value is centered in `[header bottom, cell bottom]`.
- **Size the label from the grid**, matching the native `labelSize` buckets (native label is ~19sp at standard cells, shrinking to ~15.5sp in short cells) rather than a fixed 17sp.
- Verify numeric values are **not clipped** across the supported cell row spans.

## Capabilities

### New Capabilities
None.

### Modified Capabilities
- `datafield-display`: font families, label sizing, and header/value vertical geometry SHALL match native Karoo fields (currently only "use native fonts" and "reserve a header strip" are specified, without exact families or sizing).

## Impact

- `app/src/main/res/layout/radar_datafield.xml`, `radar_datafield_left.xml`, `radar_datafield_center.xml`: font families, header probe geometry, value attributes.
- `app/src/main/kotlin/com/ukabu/karooradar/RadarDatafieldRemoteViews.kt`: label size parameter, value attributes.
- `app/src/main/kotlin/com/ukabu/karooradar/DatafieldUtils.kt`: derive/pass label size from `ViewConfig`.
- `docs/references.md`: record the native APK/font sources used for future reverse-engineering.
