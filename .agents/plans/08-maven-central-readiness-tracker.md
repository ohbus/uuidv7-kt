---
name: Maven Central Readiness Tracker
status: In progress
progress:
  - "[x] Confirm namespace"
  - "[x] Confirm POM metadata"
  - "[x] Prepare signing"
  - "[x] Prepare Central Portal account"
  - "[x] Add Central workflow"
  - "[ ] Validate first tagged deployment"
---

# Maven Central Readiness Tracker

## Goal

Prepare the project for Maven Central publishing through a guarded GitHub Actions release flow.

## Namespace

- [x] `com.subhrodip` namespace is verified.
- [x] Coordinates remain `com.subhrodip:uuidv7-kt`.
- [x] Avoid changing coordinates after the first public release unless absolutely required.

## Metadata

- [x] POM has project name.
- [x] POM has description.
- [x] POM has project URL.
- [x] POM has license.
- [x] POM has developer metadata.
- [x] POM has SCM metadata.
- [x] Sources jar is generated.
- [x] Javadocs jar is generated.

## Signing

- [x] Signing is enabled only when `signingInMemoryKey` is present.
- [x] GPG private key is available as `GPG_KEY_CONTENTS`.
- [x] Signing key id is available as `SIGNING_KEY_ID`.
- [x] No signing passphrase is required for the current key.
- [ ] Validate signed publication in the first Maven Central release run.

## Central Publishing

- [x] Central Portal account exists.
- [x] Namespace is verified.
- [x] Central publishing plugin is configured.
- [x] Maven Central publishing is restricted to the `maven-central` GitHub Environment.
- [x] Stable publishing runs only from `vX.Y.Z` tags that pass semver step validation.
- [ ] First release tag to validate: `v0.0.1`.
