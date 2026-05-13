# Karoo Radar Extension

A Hammerhead Karoo 3 extension that adds numerical radar datafields for rear vehicle detection and records the data to the FIT file.

## Why This Exists

The Karoo has a built-in radar side-view (graphical lane with dots) and audio/visual alerts, but it does **not** expose raw numbers (distance, speed, count) nor does it record radar data in the FIT file for post-ride analysis. This extension fills both gaps.

## What It Does

### Datafields

Four selectable datafields to add to any ride profile:

| Datafield | Source | Notes |
|---|---|---|
| **Vehicle Count** | Radar target ranges | Number of detected vehicles (0–8) |
| **Closest Distance** | Radar target ranges | Meters to the nearest vehicle |
| **Relative Speed** | *Derived* | Approximate closing speed (km/h or mph) |
| **Absolute Speed** | *Derived* | Vehicle's estimated road speed |

### FIT Recording

Per-second scalar developer fields written to the ride file:

- `radar_vehicles` — vehicle count
- `radar_nearest_distance_m` — closest distance in meters
- `radar_relative_speed_kmh` — closing speed in km/h
- `radar_absolute_speed_kmh` — vehicle road speed in km/h

*Note: This uses our own field schema (not mybiketraffic-compatible) because the Karoo SDK does not support array developer fields or per-lap message writes.*

### Visual Design

- Single number + small label per datafield, adapting to any grid size.
- Background color changes with threat level:
  - **Clear** — neutral
  - **Approaching** — yellow
  - **Warning** — orange
  - **Critical** — red
- `--` shown when no vehicle detected or data unavailable.

## Status

Early design phase. Implementation not yet started.

## Target Device

Hammerhead Karoo 3.

## License

TBD.
