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

## Installation

Karoo Radar is published on the Hammerhead Extension Library. Install it from there, then add the datafields to your ride profile in the Karoo app.

## Development

### Requirements

- JDK 17+
- Android SDK with platform 35 and build-tools 35.0.0
- A Hammerhead Karoo 3 for testing

### Build and install locally

```bash
./deploy.sh
```

This builds a debug APK and installs it on a Karoo connected via USB.

### Run tests

```bash
./gradlew test
```

## Releasing

Releases are automated with [release-please](https://github.com/googleapis/release-please).

1. Merge conventional commits to `main`.
2. `release-please` opens a Release PR.
3. Review and merge the Release PR.
4. `release-please` creates a git tag and GitHub Release.
5. GitHub Actions builds and signs the release APK and attaches it to the release.
6. Download the APK from the GitHub Release and upload it manually to the [Hammerhead Extension Library dashboard](https://dashboard.hammerhead.io).

See [docs/releasing.md](docs/releasing.md) for keystore setup and required GitHub Secrets.

## Target Device

Hammerhead Karoo 3.

## License

TBD.
