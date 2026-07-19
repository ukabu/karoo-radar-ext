## 1. Layout and Fonts

- [x] 1.1 Update `DatafieldUtils.kt` to use `showHeader = false` and pass label text to the RemoteViews helper.
- [x] 1.2 Update `radar_datafield.xml` (right alignment) with Barberfish-style header reference, label header, weighted value box, and native fonts.
- [x] 1.3 Update `radar_datafield_left.xml` and `radar_datafield_center.xml` similarly.
- [x] 1.4 Update `RadarDatafieldRemoteViews.kt` to set label text and color, value color, and full-cell background.

## 2. Color and Contrast

- [x] 2.1 Update `ThreatColors.kt` or the RemoteViews helper to apply the same text color to label and value.
- [x] 2.2 Confirm day/night text color is applied on CLEAR backgrounds and black text is applied on colored backgrounds.

## 3. Build and Test

- [x] 3.1 Run `./gradlew test` and `./gradlew assembleDebug` locally.
- [x] 3.2 Deploy debug APK to Karoo and verify full-cell background and native proportions.
- [x] 3.3 Take a screenshot or visually confirm next to native fields.
