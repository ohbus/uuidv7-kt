---
name: Production Readiness Tracker
status: Planned
progress:
  - "[ ] Java compatibility verified"
  - "[x] API documented"
  - "[x] Local release artifacts verified"
  - "[ ] Consumer integration verified"
  - "[ ] Rollback path verified"
---

# Production Readiness Tracker

## Goal

Define the minimum bar before publishing and consuming a stable release.

## Library Readiness

- [x] Java 8 bytecode verified by Kotlin/JVM and Java compile target configuration.
- [ ] Tests pass on Java 8.
- [ ] Tests pass on Java 11.
- [x] Tests pass on Java 17.
- [x] Tests pass on Java 21.
- [x] No runtime dependency except `kotlin-stdlib`.
- [x] Public API has Java-friendly `@JvmStatic` methods.
- [x] UUID version and variant tests pass.
- [x] Timestamp extraction tests pass.
- [x] Same-millisecond monotonicity tests pass.
- [x] Clock regression tests pass.
- [x] Sequence overflow tests pass.
- [x] Concurrent generation tests pass.

## Release Readiness

- [x] Main jar produced.
- [x] Sources jar produced.
- [x] Dokka-backed Javadocs jar produced.
- [x] Generated POM produced.
- [ ] GitHub Package published.
- [ ] GitHub Release created.
- [ ] Checksums attached.
- [ ] Release notes written.

## Consumer Readiness

- [ ] One clean sample project can consume the package.
- [ ] `chitragupta` can resolve the dependency.
- [ ] `narada` can resolve the dependency.
- [ ] Local wrapper rollback path tested.
