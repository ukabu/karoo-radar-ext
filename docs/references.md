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
