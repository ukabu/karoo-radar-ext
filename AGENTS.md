# Agent Instructions

This project is a Hammerhead Karoo 3 Extension for rear radar data display and FIT recording.

## How to Work on This Project

- **Read `CONTEXT.md`** before making any domain-level changes. New terms must be added there.
- **Read `docs/decisions.md`** before proposing alternatives. If you want to reverse a decision, call it out explicitly and justify why circumstances changed.
- **Read `docs/references.md`** for relevant SDK links, prior art, and tools.
- **Prefer simple.** No settings screen, no alerts, no per-vehicle arrays. Closest-vehicle aggregation only.

## Domain Rules

1. "Radar" means a physical ANT+ radar sensor (Varia, etc.), not an abstract concept.
2. "Datafield" = on-screen Karoo widget. "FIT Field" = recorded ride data. Keep these distinct.
3. Display units follow the Karoo system profile (metric/imperial). Whole numbers only.
4. FIT records store SI units regardless of display setting.
5. When no vehicle is detected: count = `0`, distance/speed fields = `--`, background neutral.
6. Background color is driven by threat level (CLEAR→neutral, APPROACHING→yellow, WARNING→orange, CRITICAL→red).
7. Relative speed is **derived** from distance deltas. The SDK does not expose raw radar speed.
8. Do not introduce configuration screens or user-tunable thresholds. Hardcode behavior.

## Technical Constraints (Do Not Violate)

- Karoo SDK exposes only `RADAR_TARGET_1_RANGE`…`RADAR_TARGET_8_RANGE`, `RADAR_THREAT_LEVEL`, and `RADAR_ERROR`. Nothing else.
- FIT API supports scalar `DeveloperField` values only. No arrays. No `WriteToLapMesg`.
- Target device is Karoo 3. No need to support Karoo 2.
- Use Glance for custom views (pattern from `eiradar`).

## File Locations

- `CONTEXT.md` — glossary and domain terms
- `docs/decisions.md` — design decisions
- `docs/references.md` — links and SDK docs
- `docs/adr/` — ADRs (create only when a decision is hard to reverse, surprising, and a real trade-off)

## When Adding New Terms

Update `CONTEXT.md` immediately. Do not batch. Follow the format already established there.

## When To Create an ADR

Only when all three are true:
1. Hard to reverse
2. Surprising without context
3. Result of a genuine trade-off with alternatives

If any condition is missing, skip the ADR and rely on `docs/decisions.md`.
