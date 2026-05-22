---
name: UUIDv7 Java Library Master Plan
status: Planned
progress:
  - "[ ] Confirm manual startup tasks are complete"
  - "[x] Implement minimal Kotlin UUIDv7 library"
  - "[x] Add minimal Gradle build and tests"
  - "[x] Add GitHub Actions CI, snapshot publish, and release workflows"
  - "[ ] Publish first GitHub Packages snapshot"
  - "[ ] Validate package consumption from an existing service"
  - "[ ] Cut first stable GitHub Release"
  - "[ ] Migrate duplicated service utilities to temporary wrappers"
  - "[ ] Remove duplicated service utilities after stable adoption"
---

# UUIDv7 Java Library Master Plan

## Goal

Extract the duplicated UUIDv7 utility from existing services into a standalone Kotlin/JVM library published as `com.subhrodip:uuidv7-kt`.

The library should be minimal, fast, and boring to operate:

- Kotlin implementation.
- Gradle build.
- GitHub Actions CI/CD.
- Java 8-compatible bytecode.
- Test matrix on Java 8, 11, 17, and 21.
- Runtime dependency limited to `kotlin-stdlib`.
- GitHub Packages first.
- Maven Central readiness later, not now.
- MIT license.
- No signing placeholders in the initial Gradle build.

## Non-Goals For Initial Implementation

- No Spring Boot starter.
- No benchmark module.
- No custom generator abstraction.
- No logging.
- No dependency injection.
- No code formatting plugin unless explicitly added later.
- No Maven Central publishing workflow yet.
- No Gradle signing plugin or signing configuration in v1.

## Success Criteria

- The library builds reproducibly with Gradle.
- Public API is Java-friendly through `@JvmStatic`.
- Existing UUIDv7 behavior is preserved where relevant.
- The first snapshot can be consumed by a service without removing the local duplicated class.
- Stable releases are immutable and tag-driven.

## Locked Decisions

- Use `ThreadLocalRandom` for UUID random bits. This is the best fit for high-throughput distributed systems because it avoids shared contention and OS entropy blocking. The tradeoff is that UUIDs are identifiers, not cryptographic secrets.
- `UUIDv7.parse(value)` is strict and rejects non-v7 UUIDs.
- Do not add signing placeholders until Maven Central work begins.
- Use MIT license.
- First implementation slice includes Gradle build, Kotlin source, and tests together.

## Phase Tracker

- [ ] Phase 1: Manual project startup.
- [x] Phase 2: Kotlin library implementation.
- [x] Phase 3: Gradle build and tests.
- [x] Phase 4: GitHub Actions CI/CD.
- [ ] Phase 5: GitHub Packages publishing.
- [ ] Phase 6: Service migration.
- [ ] Phase 7: Stable release.
- [ ] Phase 8: Maven Central preparation.
