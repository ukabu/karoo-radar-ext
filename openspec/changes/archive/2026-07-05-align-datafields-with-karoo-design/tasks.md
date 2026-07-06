## 1. Layout Migration

- [x] 1.1 Create XML layout files for radar datafield (right, left, center alignment).
- [x] 1.2 Create rounded-corner background shape drawable for colored threat states.
- [x] 1.3 Delete `RadarDatafieldGlance.kt` and add `RadarDatafieldRemoteViews.kt` helper.
- [x] 1.4 Update `DatafieldUtils.startRadarView()` to inflate RemoteViews instead of using Glance.

## 2. Color and Text Updates

- [x] 2.1 Update `colors.xml` with Karoo-aligned threat colors.
- [x] 2.2 Update `ThreatColors.kt` to return Compose Color values matching the new palette and add black text-on-background helper.
- [x] 2.3 Add day/night detection for default text color in CLEAR state.

## 3. Value Formatting

- [x] 3.1 Remove unit suffixes from value strings in all four datafield types.
- [x] 3.2 Keep imperial/metric conversion for numeric values.

## 4. Build and Dependency Updates

- [x] 4.1 Remove Glance dependency from `app/build.gradle.kts` and `libs.versions.toml`.
- [x] 4.2 Run `./gradlew test` and `./gradlew assembleDebug` to verify the build.

## 5. Verification

- [x] 5.1 Verify XML layouts render without errors.
- [x] 5.2 Confirm unit tests still pass.
