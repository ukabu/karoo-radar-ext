# Context: Karoo Radar Extension

## Glossary

### Radar
A physical rear-vehicle detection device that implements the ANT+ radar sensor profile. Examples include the Garmin Varia RTL515 and MicroShift AirShift. The device detects approaching vehicles from behind and transmits real-time telemetry.

### Radar Sensor Profile
The ANT+ device profile that defines how radar data is encoded and transmitted. The profile includes threat level, vehicle distance, relative speed, and number of detected vehicles.

### Datafield
A custom on-screen display widget within a Karoo ride profile. Datafields show ride metrics to the cyclist during an activity.

### FIT Field
Data recorded in the FIT (Flexible and Interoperable Data Transfer) file for post-ride analysis. FIT fields are written alongside standard ride data and are viewable in third-party tools.

### Side-View
The built-in Karoo radar visualization — a graphical lane view rendered at the edge of the screen showing approaching vehicles as dots. This is distinct from numerical datafields.

### Closest Vehicle
The nearest detected vehicle behind the cyclist. For display and recording purposes, this is the primary vehicle of interest when multiple vehicles are present.

### Vehicle Count
The total number of vehicles currently detected by the radar. Exposed as a selectable datafield.

### Closest Vehicle Distance
The distance in meters from the cyclist to the nearest detected vehicle. Exposed as a selectable datafield.

### Closest Vehicle Relative Speed
The speed at which the nearest detected vehicle is approaching the cyclist, expressed in km/h. Exposed as a selectable datafield.

### Closest Vehicle Absolute Speed
The actual road speed of the nearest detected vehicle (cyclist ground speed + relative speed), expressed in km/h. Exposed as a selectable datafield.

### Threat Level
The danger level reported by the ANT+ radar profile, on a scale from 0 (no threat) to 3 (critical). Used to drive the background color of datafields as a visual alert, rather than displayed as a raw number.

### Radar Relative Speed (Derived)
The speed at which the nearest detected vehicle is approaching the cyclist, expressed in km/h. **Derived** because the Karoo Extension SDK does not expose raw radar relative speed. Computed as a smoothed exponential moving average of `-dD/dt` over approximately 3 seconds, capped at 200 km/h, with negative values clamped to 0. The derivative window is reset and the field shows `--` for 1–2 seconds when the closest vehicle changes (handoff), to avoid displaying garbage during transitions.

### Datafield Layout
A single size-agnostic Glance composable per datafield. Each cell shows a small localized label above or below a large centered numeric value. The background color changes with threat level. Four independent datafield types are registered: Vehicle Count, Closest Vehicle Distance, Closest Vehicle Relative Speed, Closest Vehicle Absolute Speed.

### Threat Color Mapping
The background color of each datafield is driven by the current threat level:
- **CLEAR** (0): default/neutral background (no color override).
- **APPROACHING** (1): yellow.
- **WARNING** (2): orange.
- **CRITICAL** (3): red.
When multiple vehicles are detected, the highest threat level among all vehicles determines the background color.

### Settings Policy
No configuration screen. Threat level thresholds follow the native ANT+ radar profile (0–3) without user customization. The extension is purely for data display and FIT recording; the Karoo system already handles audio/visual radar alerts natively.

### Radar Developer Field Schema
The set of custom scalar developer fields written to the FIT file. Owned by this extension (not mybiketraffic-compatible). Fields:
- `radar_vehicles` (uint8) — number of detected vehicles at this timestamp.
- `radar_nearest_distance_m` (uint16) — closest vehicle distance in meters.
- `radar_relative_speed_kmh` (uint8) — derived closest-vehicle relative speed in km/h.
- `radar_absolute_speed_kmh` (uint8) — derived closest-vehicle absolute speed in km/h, requiring cyclist ground speed from the Karoo SPEED stream. Shows `--` when cyclist speed is unavailable.

### No-Vehicle Display State
The visual state of distance, relative speed, and absolute speed datafields when the radar detects zero vehicles. Displayed as `--` (or empty) rather than `0` to avoid implying a vehicle is present at zero distance/speed.

### Target Device
Hammerhead Karoo 3 (minimum). Karoo 2 is not targeted due to lack of test hardware. The actual minimum supported API level will be determined by the `karoo-ext` SDK requirement during build setup.

### FIT Recording Behavior
Developer field writes to FIT follow the ride state and radar connection state:
- On **RideState.Recording** and radar connected: write current values at 1 Hz.
- On **RideState.Paused** (manual or auto): stop writing developer fields. No phantom radar data during inactive periods.
- On radar **disconnect** during an active ride: skip writing developer fields for that second. Records resume when the radar reconnects.
- The extension subscribes to `RideState` to gate FIT emission accordingly.

### Unit Policy
Display units follow the Karoo system preference (metric or imperial). Datafields show whole-number values only. FIT file records store canonical SI units regardless of display setting.
