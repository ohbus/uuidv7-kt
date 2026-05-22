---
name: Publishing Plan
status: Planned
progress:
  - "[x] Publish first snapshot to GitHub Packages"
  - "[ ] Consume snapshot from one service"
  - "[ ] Publish first stable release to GitHub Packages and Maven Central"
  - "[ ] Verify release assets"
  - "[ ] Document consumer setup"
---

# Publishing Plan

## Goal

Publish `com.subhrodip:uuidv7-kt` through GitHub Packages snapshots and Maven Central guarded stable releases.

## Coordinates

- Group: `com.subhrodip`
- Artifact: `uuidv7-kt`
- Main branch snapshot: `0.0.1-SNAPSHOT`
- First stable release: `0.0.1`

## GitHub Packages

Repository URL:

```text
https://maven.pkg.github.com/ohbus/uuidv7-kt
```

Publishing credentials:

- Use `GITHUB_ACTOR`.
- Use `GITHUB_TOKEN`.
- Do not create a personal access token for publishing from this repository unless GitHub permissions require it.
- Snapshot publishing must use `publishAllPublicationsToGitHubPackagesRepository`, not generic `publish`.

## Maven Central

- Stable Central publishing runs from the manual release workflow `release_name` input only.
- The release input name must match `vX.Y.Z`.
- The workflow creates the matching Git tag only after package publication succeeds.
- Publishing requires the `maven-central` GitHub Environment approval.
- Credentials come from `MAVEN_CENTRAL_USERNAME` and `MAVEN_CENTRAL_PASSWORD`.
- Signing uses in-memory `GPG_KEY_CONTENTS`, `SIGNING_KEY_ID`, and optional `SIGNING_PASSWORD`.
- `SIGNING_PASSWORD` may be blank when the GPG key has no passphrase.
- Whitespace-only `SIGNING_PASSWORD` values are normalized to blank before Gradle runs.
- Central publishing must use `publishAndReleaseToMavenCentral`.

## Consumer Rules

- Consumers must pin exact versions.
- Consumers must not use dynamic versions.
- Snapshot adoption must happen before stable release adoption.
- Existing services must keep local UUIDv7 implementation until dependency usage is verified.

## Release Assets

Each stable GitHub Release should include:

- Main jar.
- Sources jar.
- Javadocs jar.
- POM file.
- SHA-256 checksum file.
