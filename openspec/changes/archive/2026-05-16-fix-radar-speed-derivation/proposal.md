## Why

Post-ride testing revealed two bugs in the radar speed datafields: the relative speed display jumped wildly as vehicles approached (due to noisy point-to-point distance derivatives), and the absolute speed field never showed any value at all (due to reading the wrong SDK field key). Both issues degrade the rider's situational awareness and corrupt FIT-recorded data.

## What Changes

- Fix `RadarProcessor.subscribeSpeed()` to read cyclist ground speed from `DataType.Field.SPEED` instead of `DataType.Field.SINGLE`. This restores absolute vehicle speed computation.
- Replace the point-to-point distance derivative in `deriveRelativeSpeed()` with a **2-second sliding window** of distance samples. Speed is computed only when the window spans ≥ 1 second, comparing oldest to newest sample. The existing EMA (α=0.3) still smooths on top.
- Reset the EMA and clear the window on vehicle handoff or when no vehicles are detected.

## Capabilities

### New Capabilities
- *(none — this is a bug fix within existing capabilities)*

### Modified Capabilities
- *(none — no spec-level requirement changes; the existing behavior contract in `CONTEXT.md` already describes the intended derivation. This change corrects the implementation to match it.)*

## Impact

- `app/src/main/kotlin/com/ukabu/karooradar/RadarProcessor.kt` — internal field key and algorithm changes only.
- No API changes, no new datafields, no FIT schema changes.
- Existing ride profiles using the relative-speed or absolute-speed datafields will display correctly after upgrade.
