## 1. Confirm native metrics on device

- [x] 1.1 Add a temporary debug log in `DatafieldUtils.startRadarView` that prints `config.gridSize`, `config.viewSize`, and `config.textSize` on every invocation
- [x] 1.2 Deploy the debug build, open a ride page, `adb logcat` the values, and record them
- [x] 1.3 Confirm whether `startView` re-fires with a new `textSize` when rows are added, and record the STANDARD/SHORT branch (device reports `h402dp`)
- [x] 1.4 Record the observed `textSize` per row span and derive the label-size ratio (label ≈ ? × value); remove the temporary log

## 2. Fix fonts and value/label styling

- [x] 2.1 In all three layouts (`radar_datafield.xml`, `_left.xml`, `_center.xml`) set the value `fontFamily` to `relative`
- [x] 2.2 Set the label `fontFamily` to `ibm-plex-sans-condensed` and remove `textStyle="bold"`
- [x] 2.3 Set `letterSpacing="-0.04"`, `singleLine`/`maxLines="1"`, and `includeFontPadding="false"` on the value
- [x] 2.4 Set `textAllCaps="true"`, `includeFontPadding="false"`, and `lineSpacingMultiplier="0.7"` on the label

## 3. Fix header/value vertical geometry

- [x] 3.1 Replace the `header_ref` `"M"` + margin probe with a header region that reserves exactly the native header height (22dp min), no extra top margin
- [x] 3.2 Center the value in `[header bottom, cell bottom]` using a bottom-anchored value container (approximating native's `height=0dp` fill)
- [x] 3.3 Verify the value's vertical center matches a neighbouring native field of the same grid size

## 4. Grid-aware label sizing

- [x] 4.1 Add label-size derivation in `DatafieldUtils` (from `config.textSize` using the ratio from task 1.4, clamped to sane bounds)
- [x] 4.2 Pass the derived label size into `radarDatafieldRemoteViews`
- [x] 4.3 Apply the label size with `setTextViewTextSize(COMPLEX_UNIT_SP)` on `field_label`; keep the value using `config.textSize`

## 5. Documentation

- [x] 5.1 Record the reverse-engineering sources (ride APK path, `/system/etc/fonts.xml`, decompiled classes/styles) in `docs/references.md`

## 6. Verify on device

- [x] 6.1 Run `./gradlew test` and `./gradlew assembleDebug`
- [x] 6.2 Deploy the debug APK to the Karoo and screenshot a ride page
- [x] 6.3 Compare our field against a neighbouring native field at a standard cell (label family/weight/width and value centering match)
- [x] 6.4 Re-check at the shortest available cell and confirm the numeric value (or `--`) is not clipped at the bottom
- [x] 6.5 Capture before/after screenshots for the change record
