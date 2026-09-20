## 1. Make build-release reusable

- [ ] 1.1 Add a `workflow_call` trigger to `build-release.yml` with a required string input `tag`
- [ ] 1.2 Resolve the version from `github.event.release.tag_name || inputs.tag || github.event.inputs.tag || github.ref_name`
- [ ] 1.3 Confirm the signing secrets are available to a `workflow_call` invocation (map explicitly or use `secrets: inherit`)

## 2. Trigger the build from release-please

- [ ] 2.1 Ensure `release-please.yml` has `permissions: contents: write`
- [ ] 2.2 Add a `build-release` job with `needs: release-please` and `if: needs.release-please.outputs.release_created == 'true'`
- [ ] 2.3 Call `./.github/workflows/build-release.yml` with `with: tag: ${{ needs.release-please.outputs.tag_name }}` and the signing secrets

## 3. Verify

- [ ] 3.1 Push a releasable commit, merge the resulting Release PR, and confirm `Build Release APK` runs in the same workflow run and attaches the APK
- [ ] 3.2 Confirm manual `workflow_dispatch` still builds and attaches an APK for an existing tag
- [ ] 3.3 Update `docs/releasing.md` / `README.md` to state the APK build is automated (only the dashboard upload is manual)
