# Vince's Patches

Unofficial Morphe patches for the Reddit app. The project name must never imply
it is an official Morphe project (see `NOTICE`).

## House rules

- **Work on the `dev` branch.** Commit and push there. Never push to `main`;
  stable releases happen by merging `dev` into `main` (merge, never squash).
- **Commit messages use exactly one of three prefixes:**
  - `feat: ...` a new user-facing patch or feature (triggers a pre-release)
  - `fix: ...` a user-facing bug fix (triggers a pre-release)
  - `chore: ...` anything users don't need in the changelog: build, docs, refactors (no release)
- **Never hand-edit generated files:** `patches-list.json`, `patches-bundle.json`,
  `CHANGELOG.md`, and the README section between `PATCHES_START` / `PATCHES_END`.
  `release.yml` regenerates them.
- **Don't rewrite `.github/workflows/release.yml`.** If release behaviour really
  must change, make a minimal edit to it or `.releaserc`, never a new release script.
  Never create releases or tags by hand, and never force-push release commits.

## Writing patches

- Use [MorpheApp/morphe-patches](https://github.com/MorpheApp/morphe-patches) as
  the reference for how patches and fingerprints are written. It already contains
  Reddit patches (`patches/src/main/kotlin/app/morphe/patches/reddit/`): check
  there first for existing fingerprints, shared utilities and conventions, and
  follow the same structure (`Fingerprints.kt` next to the patch file).
  Read it with: `git clone --depth 1 https://github.com/MorpheApp/morphe-patches`
- Patcher docs: https://github.com/MorpheApp/morphe-patcher/tree/main/docs
- Patch code lives in `patches/src/main/kotlin/`; Java code injected into the app
  (extensions) lives in `extensions/extension/src/main/java/`.

## Building

```sh
./gradlew buildAndroid   # output: patches/build/libs/patches-*.mpp
```

The build needs:
- Network access to `maven.pkg.github.com`, `dl.google.com` and `jitpack.io`.
- GitHub Packages credentials: `gpr.user` / `gpr.key` in `~/.gradle/gradle.properties`
  (a classic PAT with `read:packages`), or `GITHUB_ACTOR` / `GITHUB_TOKEN`.
- An Android SDK with platform 36 (`ANDROID_HOME`).

`scripts/cloud-setup.sh` sets all of this up in a Claude Code cloud environment.
