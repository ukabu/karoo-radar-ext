## 1. Pass textSize and apply fonts

- [x] 1.1 Update `DatafieldUtils.kt` to pass `config.textSize` to `radarDatafieldRemoteViews`.
- [x] 1.2 Update `RadarDatafieldRemoteViews.kt` signature to accept `textSize: Float` and apply it to `field_value`.
- [x] 1.3 Set the header label text size to 10sp in the three layout XML files.
- [x] 1.4 Bump header label text size from 10sp to 13sp.
- [x] 1.5 Use Ping LCG Medium for the header label.
- [x] 1.6 Bump header label text size from 13sp to 15sp and use Ping LCG Bold.
- [x] 1.7 Bump header label text size from 15sp to 17sp and add top padding to header.
- [x] 1.8 Switch label font to Ping L and adjust top/right padding to 7dp/3dp.

## 2. Build and test

- [x] 2.1 Run `./gradlew test` and `./gradlew assembleDebug` locally.
- [x] 2.2 Deploy to Karoo and verify the value fills the cell vertically.
