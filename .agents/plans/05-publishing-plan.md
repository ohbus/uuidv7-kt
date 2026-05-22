---
name: Publishing Plan
status: Planned
progress:
  - "[ ] Publish first snapshot to GitHub Packages"
  - "[ ] Consume snapshot from one service"
  - "[ ] Publish first stable release"
  - "[ ] Verify release assets"
  - "[ ] Document consumer setup"
---

# Publishing Plan

## Goal

Publish `com.subhrodip:uuidv7-kt` through GitHub Packages first, with future Maven Central readiness.

## Coordinates

- Group: `com.subhrodip`
- Artifact: `uuidv7-kt`
- Initial snapshot: `0.1.0-SNAPSHOT`
- First stable release: `0.1.0`

## GitHub Packages

Repository URL:

```text
https://maven.pkg.github.com/ohbus/uuidv7-kt
```

Publishing credentials:

- Use `GITHUB_ACTOR`.
- Use `GITHUB_TOKEN`.
- Do not create a personal access token for publishing from this repository unless GitHub permissions require it.

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
