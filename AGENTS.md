# Agent Guide

## Project Goal

Build a minimal, fast Kotlin/JVM UUIDv7 library published as `com.subhrodip:uuidv7-kt`.

The library should:

- Use Kotlin with Java-friendly `@JvmStatic` APIs.
- Compile to Java 8-compatible bytecode.
- Test on Java 8, 11, 17, and 21.
- Use Gradle and GitHub Actions.
- Keep runtime dependencies limited to `kotlin-stdlib`.
- Publish to GitHub Packages first.
- Prepare for Maven Central later without implementing Central publishing initially.
- Use MIT license.
- Do not add signing placeholders in the initial Gradle build.

## Required Starting Point

Read these files before implementing anything:

1. `.agents/plans/00-master-plan.md`
2. `.agents/plans/01-manual-startup-tracker.md`
3. `.agents/plans/02-library-implementation-plan.md`
4. `.agents/plans/03-gradle-build-plan.md`
5. `.agents/plans/04-github-actions-release-plan.md`

Use the remaining trackers for release, migration, and readiness work:

- `.agents/plans/05-publishing-plan.md`
- `.agents/plans/06-migration-rollout-plan.md`
- `.agents/plans/07-production-readiness-tracker.md`
- `.agents/plans/08-maven-central-readiness-tracker.md`
- `.agents/plans/09-security-and-governance-tracker.md`
- `.agents/plans/10-deprecation-strategy.md`
- `.agents/plans/11-test-hardening-plan.md`

## Implementation Order

Implement in this order unless the user explicitly changes scope:

1. Kotlin UUIDv7 source and tests.
2. Minimal Gradle build.
3. Harden tests using `.agents/plans/11-test-hardening-plan.md`.
4. README, LICENSE, SECURITY, and CHANGELOG.
5. GitHub Actions CI.
6. GitHub Packages snapshot publishing.
7. GitHub Release workflow.
8. Service migration wrappers in downstream projects.

## Current Constraint

The initial repository bootstrap intentionally created plans only. Do not write code or configuration unless the user has explicitly approved the next phase.

## Source Layout To Create

When implementation is approved, use this structure:

```text
src/main/kotlin/com/subhrodip/uuidv7/UUIDv7.kt
src/test/kotlin/com/subhrodip/uuidv7/UUIDv7Test.kt
```

Expected build files:

```text
settings.gradle.kts
gradle.properties
build.gradle.kts
```

Expected workflow files:

```text
.github/workflows/ci.yml
.github/workflows/publish.yml
.github/workflows/release.yml
```

## Public API

The first implementation should expose:

- `UUIDv7.generate()`
- `UUIDv7.randomUUID()`
- `UUIDv7.parse(value)`
- `UUIDv7.isValid(value)`
- `UUIDv7.isVersion7(uuid)`
- `UUIDv7.extractUnixTimestamp(uuid)`
- `UUIDv7.extractInstant(uuid)`

Keep the API small. Do not add configurable generators, Spring integration, benchmarks, logging, or extra modules in v1.

`UUIDv7.parse(value)` is strict: it should reject valid UUID strings that are not UUIDv7 values.

Use `ThreadLocalRandom` for random bits in v1. This is the throughput-oriented choice for IDs in high-volume services. Document that generated UUIDs are not suitable as secrets or security tokens.

## Manual User Tasks

Track manual work in `.agents/plans/01-manual-startup-tracker.md`.

Important manual tasks include:

- Create GitHub repository `ohbus/uuidv7-kt`.
- Add remote origin.
- Configure branch protection.
- Confirm GitHub Packages permissions.
- Decide when to make the repository public.
- Prepare Maven Central namespace and signing later.

## Verification

After code/config implementation, expected checks are:

```powershell
./gradlew clean check
./gradlew clean check -PtestJavaVersion=8
./gradlew clean check -PtestJavaVersion=11
./gradlew clean check -PtestJavaVersion=17
./gradlew clean check -PtestJavaVersion=21
```

On Windows, use `.\gradlew.bat` once the wrapper exists.
