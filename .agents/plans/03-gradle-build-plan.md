---
name: Gradle Build Plan
status: Planned
progress:
  - "[x] Add settings.gradle.kts"
  - "[x] Add gradle.properties"
  - "[x] Add build.gradle.kts"
  - "[x] Configure Kotlin JVM plugin"
  - "[x] Configure Maven publishing"
  - "[x] Verify local build"
---

# Gradle Build Plan

## Goal

Create a minimal Gradle Kotlin DSL build for a Kotlin/JVM library.

## Build Constraints

- Use Gradle.
- Use Kotlin JVM plugin.
- Use Dokka Javadoc plugin for Kotlin API documentation.
- Use `java-library`.
- Use `maven-publish`.
- Do not use `signing` in the initial build.
- Compile to Java 8-compatible bytecode.
- Run Gradle itself on JDK 17.
- Test on Java 8, 11, 17, and 21 through toolchains.

## Dependencies

Runtime:

- `org.jetbrains.kotlin:kotlin-stdlib`

Test only:

- JUnit Jupiter.

Avoid initially:

- AssertJ.
- Mocking libraries.
- Formatting plugins.
- Static analysis plugins.
- Benchmark plugins.

## Artifacts

The build should produce:

- Main jar.
- Sources jar.
- Dokka-backed Javadocs jar.
- Maven publication metadata.

## Publishing Metadata

Use:

- Group: `com.subhrodip`
- Artifact: `uuidv7-kt`
- Module name: `com.subhrodip.uuidv7`
- License: MIT
- SCM: `https://github.com/ohbus/uuidv7-kt`

## Reproducibility

Configure archive tasks with:

- Preserved file timestamps disabled.
- Reproducible file order enabled.

## First Implementation Slice

Implement Gradle build, Kotlin source, and tests together. A build skeleton without behavior is not useful, and source without a build cannot be verified.

## Verification Status

- [x] `.\gradlew.bat clean check` passes on the available default JDK.
- [x] `.\gradlew.bat check -PtestJavaVersion=17` passes.
- [x] `.\gradlew.bat check -PtestJavaVersion=21` passes.
- [ ] Java 8 matrix check is blocked until a local JDK 8 exists or Gradle toolchain download repositories are configured.
- [ ] Java 11 matrix check is blocked until a local JDK 11 exists or Gradle toolchain download repositories are configured.
