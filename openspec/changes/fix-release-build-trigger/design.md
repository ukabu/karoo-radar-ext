## Context

The release pipeline is two workflows:

- `release-please.yml` — on push to `main`, runs `googleapis/release-please-action@v4` with `secrets.GITHUB_TOKEN`. On merging its Release PR it creates a git tag and a GitHub Release.
- `build-release.yml` — triggers on `release: [published]`, `push: tags: v*.*.*`, and `workflow_dispatch`; builds `assembleRelease` with the signing secrets and uploads the APK via `softprops/action-gh-release`.

GitHub's recursion guard: "events triggered by the `GITHUB_TOKEN` will not create a new workflow run." release-please's tag/release are created with `GITHUB_TOKEN`, so neither the `release` nor the tag `push` trigger fires `build-release`. This was confirmed on v1.2.2: after merging PR #4, no `Build Release APK` run was created until it was dispatched manually.

## Goals / Non-Goals

**Goals:**
- The signed APK build runs automatically when release-please publishes a release, with no manual step.
- Keep manual `workflow_dispatch` working (used to rebuild an APK for an existing tag).
- No new long-lived secret (avoid a PAT) if possible.

**Non-Goals:**
- Automating the upload to the Hammerhead Extension Library dashboard (no API; stays manual).
- Changing versioning/changelog behaviour.

## Decisions

### 1. Trigger the build within the release-please run, not via a new event
Options considered:

- **(A) PAT for release-please.** Give release-please a fine-grained PAT so its release/tag events trigger workflows. Works, but adds a long-lived credential to manage and rotate. Rejected as the default.
- **(B) Cross-workflow `gh workflow run`.** Have a step in `release-please.yml` call the dispatch API. Depends on whether `GITHUB_TOKEN` may raise `workflow_dispatch`; behaviour is inconsistent across setups. Rejected.
- **(C) Reusable workflow job (chosen).** Convert `build-release.yml` to also accept `workflow_call`, and add a job in `release-please.yml` that `uses: ./.github/workflows/build-release.yml` when `needs.release-please.outputs.release_created == 'true'`. Jobs within the same run are not subject to the recursion guard, so this is reliable and needs no new secret.

### 2. Pass the tag explicitly
`release-please-action@v4` exposes `release_created` (boolean) and `tag_name` outputs. The reusable-workflow job passes `tag_name` via a `tag` input; `build-release.yml` already resolves the tag from `github.event.release.tag_name || github.event.inputs.tag || github.ref_name`, which we extend to also read the `workflow_call` input.

### 3. Keep the existing triggers
Retain `release`, tag-push, and `workflow_dispatch` so manual rebuilds and any external tag pushes still work. With the recursion guard, the event triggers will simply not fire for release-please's own releases — the reusable job covers that case.

## Risks / Trade-offs

- **[Duplicate builds if the event trigger ever does fire]** → rely on the recursion guard (it fires reliably for `GITHUB_TOKEN`-created releases); if a PAT is ever introduced, drop the reusable job.
- **[Duplicated secrets wiring]** → the reusable call must forward the four signing secrets (`secrets: inherit` or explicit mapping); a missing secret silently produces an unsigned `assembleRelease` that fails signing — verify by watching the first automated run.
- **[Chained jobs share the workflow's permissions]** → ensure `permissions: contents: write` is set on the release-please workflow so the called job can attach assets.

## Migration Plan

1. Add `workflow_call` + `tag` input to `build-release.yml`.
2. Add the conditional `build-release` job to `release-please.yml`.
3. Verify on the next release: merge a Release PR and confirm `Build Release APK` runs in the same run and attaches the APK. Rollback is a revert of the two workflow files; manual dispatch remains available meanwhile.

## Open Questions

- Whether `secrets: inherit` is sufficient for the reusable workflow, or the four signing secrets must be mapped explicitly.
- Whether to eventually simplify to a single workflow (release-please + build jobs) now that the build is a job in the same run.
