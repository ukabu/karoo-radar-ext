# References

Links and resources discovered or consulted throughout the design of this extension.

## MyBikeTraffic

- **Website**: https://www.mybiketraffic.com — The service we initially aimed for FIT-compatibility with.
- **Data example page**: https://www.mybiketraffic.com/example/ — Shows what radar data looks like post-ride.

## Hammerhead / Karoo SDK

- **Official Karoo Extension SDK**: https://github.com/hammerheadnav/karoo-ext — Source code for `karoo-ext`, includes `FitEffect.kt`, `DataType.kt`, etc.
- **Karoo SDK docs / overview**: https://hammerhead.io (dashboard at https://dashboard.hammerhead.io)
- **Sample extension usage** in `karoo-ext` repo:
  - `FitEffect.kt` — FIT write API (`DeveloperField`, `WriteToRecordMesg`, etc.)
  - `DataType.kt` — Available data streams including `RADAR` with `RADAR_TARGET_1_RANGE` through `RADAR_TARGET_8_RANGE`
  - `DataTypeImpl.kt` — Base class for custom datafields

## Existing Radar Extensions (Reference Code)

- **eiRadar** (yrkan): https://github.com/yrkan/eiradar — Full-featured radar extension with FIT recording, Glance widgets, alerts. Used extensively as a reference for:
  - `VariaRadarExtension.kt` — Extension lifecycle, FIT `startFit()`, `RideState` tracking
  - `RadarEngine.kt` — How to consume `DataType.Type.RADAR` stream
  - `GlanceDataType.kt` — Glance-based custom widget architecture
- **kxradar** (itxsvv / foobatz): https://github.com/itxsvv/kxradar — Simpler radar sound configuration extension. Points to the SDK repo.

## Awesome Karoo

- **timklge/awesome-karoo**: https://github.com/timklge/awesome-karoo — Curated list of community Karoo extensions.

## Garmin FIT SDK

- **FIT Overview**: https://developer.garmin.com/fit/overview/
- **Developer Data Fields Cookbook**: https://developer.garmin.com/fit/cookbook/developer-data/ — How custom developer fields work in FIT.
- **Node.js FIT parser**: `@garmin/fitsdk` npm package — Used to reverse-engineer the mybiketraffic FIT file.

## Tools Used

- **FIT file parsing**: Node.js `@garmin/fitsdk` package via `npx` in `/tmp`
- **Sample FIT file**: `/home/ukabu/21028470940_ACTIVITY.fit` — Real ride file with mybiketraffic developer fields, used to confirm their schema (arrays of 8 ranges/speeds, scalar passing speeds, lap/session totals).

## Karoo Native Field Reverse-Engineering

Sources used to derive the native datafield metrics (fonts, sizing, vertical geometry). Re-verify against these when a firmware update changes field rendering.

- **Ride app APK**: `io.hammerhead.rideapp` → pulled from `/system/priv-app/ride/ride.apk` on the connected Karoo 3. Decompiled with [jadx](https://github.com/skylot/jadx); resources dumped with `aapt2 dump resources` / `aapt2 dump xmltree`.
- **System font config**: `/system/etc/fonts.xml` (the only font configuration on the device). Registered families:
  - Value: alias `relative` → `monospace` → `Relative12-Regular.otf`
  - Label: alias `ibm-plex-sans-condensed` → `ibm-sans-cond` → `IBMPlexSansCondensed-Medium.otf`
  - Note: `Ping L`, `Ping LCG`, and `Hammerhead Relative Mono 12` are **not** registered families.
- **Native layout / styles** (obfuscated resource names recovered via `aapt2`):
  - `res/layout/data_element_single` (obfuscated `res/Jb.xml`) — constant-tile numeric field anatomy.
  - Styles: `dataElementRoot` (background `data_element_background`, radius 10dp; foreground `data_element_foreground`, 1px stroke), `dataHeader` (minHeight 22dp), `dataHeaderTextStyle` (17sp default, allCaps, no bold, `includeFontPadding=false`, `lineSpacingMultiplier=0.7`), `singleNumericDataStyle` (color, `letterSpacing=-0.04`, singleLine, no textSize).
  - Custom view `io.hammerhead.dataelements.views.DataHeaderView`.
- **Host sizing model** (decompiled classes):
  - `m6.e.a()` builds `DataElementConstraints(dataSize, dataTranslationY, labelSize, singleLineLabel, …, labelLineSpacingMultiplier, labelTranslationY)` keyed on row/column span and `screenHeightDp` (`>400` → `STANDARD`, else `SHORT`).
  - `l6.g.a(constraints)` applies `dataSize` to the value (`setTextSize`) and passes `constraints` to `DataHeaderView.m()` for the label.
  - `ka.v` (case 1) derives `ViewConfig.textSize = dataSize / density` and emits a new `ViewConfig` per grid change via `switchMap`.
  - SDK `ViewConfig.textSize` documented as "Font size used in standard numeric view of this grid size in sp".
- **On-device observation** (Karoo 3, `h402dp` → STANDARD): a half-width `rowSpan=12` cell logs `gridSize=(30, 12) viewSize=(238, 126) textSize=41`.
