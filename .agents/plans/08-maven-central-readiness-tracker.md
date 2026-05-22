---
name: Maven Central Readiness Tracker
status: Planned
progress:
  - "[ ] Confirm namespace"
  - "[ ] Confirm POM metadata"
  - "[ ] Prepare signing later"
  - "[ ] Prepare Central Portal account"
  - "[ ] Add Central workflow later"
---

# Maven Central Readiness Tracker

## Goal

Prepare the project for Maven Central without implementing Maven Central publishing in v1.

## Namespace

- [ ] Confirm whether `com.subhrodip` can be verified.
- [ ] If not, use `io.github.subhrodip` for Maven Central.
- [ ] Avoid changing coordinates after `1.0.0` unless absolutely required.

## Metadata

- [ ] POM has project name.
- [ ] POM has description.
- [ ] POM has project URL.
- [ ] POM has license.
- [ ] POM has developer metadata.
- [ ] POM has SCM metadata.
- [ ] Sources jar is generated.
- [ ] Javadocs jar is generated.

## Signing

- [ ] Keep signing out of the initial Gradle build.
- [ ] Generate GPG key.
- [ ] Store private key as GitHub secret only when needed.
- [ ] Store passphrase as GitHub secret only when needed.
- [ ] Validate signed local publication before Central release.

## Central Publishing

- [ ] Create Sonatype Central Portal account.
- [ ] Verify namespace.
- [ ] Add Central publishing plugin only when ready.
- [ ] Start with manual workflow dispatch.
- [ ] Automate only after at least one successful manual Central publish.
