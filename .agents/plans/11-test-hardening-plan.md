---
name: Test Hardening Plan
status: Planned
progress:
  - "[ ] Add RFC bit-layout tests"
  - "[ ] Add repeated invariant tests"
  - "[ ] Add Java interop tests"
  - "[ ] Add null-contract tests"
  - "[ ] Add stronger concurrency tests"
  - "[ ] Add timeout guards to hot-loop tests"
  - "[ ] Decide benchmark scope separately"
---

# Test Hardening Plan

## Goal

Raise the UUIDv7 test suite from good internal-snapshot coverage to public-library coverage.

The current tests already cover generation, strict parsing, timestamp extraction, same-millisecond monotonicity, clock regression, sequence overflow, and concurrent uniqueness. This plan adds stronger RFC layout coverage, Java interop coverage, repeated invariant checks, and clearer timeout bounds.

## Inspiration To Adopt

Use the following ideas from the referenced Java UUIDv7 test suite:

- Direct timestamp extraction from `mostSignificantBits`.
- Explicit version-bit test.
- Explicit variant-bit test.
- timestamp close-to-now test with a small tolerance.
- repeated timestamp monotonicity test.
- 100k uniqueness test with timeout.
- repeated same-millisecond distinctness test.
- entropy-bit variation check.
- repeated version and variant invariant check.
- timestamp 48-bit masking test.

Adapt these to this library's design instead of copying them blindly.

## Important Differences From The Referenced Tests

- This library uses `UUIDv7.generate()` as the primary API and `randomUUID()` as a migration alias.
- This library has strict `parse(value)`, `isValid(value)`, `isVersion7(uuid)`, `extractUnixTimestamp(uuid)`, and `extractInstant(uuid)` APIs that also need tests.
- This library intentionally uses 12-bit monotonic `rand_a`, so same-millisecond UUIDs should not merely be distinct; their `rand_a` sequence should increase until overflow.
- This library does not claim cryptographic entropy. Tests should verify variation, not security.

## Planned Test Groups

### RFC Bit Layout

Add tests that inspect UUID fields without `ByteBuffer`:

- Timestamp is exactly `uuid.mostSignificantBits ushr 16`.
- Version nibble is exactly `0x7`.
- `rand_a` is exactly the low 12 bits of `mostSignificantBits`.
- Variant top bits are `10`.
- `rand_b` is constrained to the low 62 bits of `leastSignificantBits`.

Acceptance criteria:

- Tests prove the produced UUID layout matches RFC 9562 UUIDv7 field positions.
- Tests do not use byte arrays or `ByteBuffer`.

### Repeated Invariants

Add bounded hot-loop tests:

- Generate 10,000 UUIDs and assert `version() == 7`.
- Generate 10,000 UUIDs and assert `variant() == 2`.
- Generate 1,000 UUIDs and assert extracted timestamps are non-decreasing.
- Generate 100,000 UUIDs and assert uniqueness.

Acceptance criteria:

- Use `@Timeout` to prevent slow or stuck tests.
- Keep counts high enough to catch obvious regressions but small enough for CI.

### Same-Millisecond Behavior

Improve the current same-ms tests:

- Start on a fresh millisecond.
- Generate two UUIDs immediately.
- If timestamps match, assert UUIDs differ.
- If timestamps match, assert `rand_a` increases.
- If timestamps differ, assert timestamp increased.
- Add repeated test coverage to reduce false confidence from a single timing window.

Acceptance criteria:

- Same-ms behavior is tested as monotonic, not just unique.
- Test remains tolerant when the clock ticks between calls.

### Entropy Variation

Add variation tests for the random portion:

- When two UUIDs share the same timestamp, compare low 62 `rand_b` bits.
- Assert `rand_b` differs for at least one same-ms pair across repeated attempts.
- Do not assert cryptographic unpredictability or exact distribution.

Acceptance criteria:

- Test catches an accidental constant `rand_b`.
- Test does not fail just because two immediate calls land in different milliseconds.

### Java Interop

Add a Java test source:

```text
src/test/java/com/subhrodip/uuidv7/UUIDv7JavaInteropTest.java
```

Cover:

- `UUIDv7.generate()` compiles from Java.
- `UUIDv7.randomUUID()` compiles from Java.
- `UUIDv7.parse(String)` compiles from Java.
- `UUIDv7.isValid(String)` compiles from Java.
- `UUIDv7.extractUnixTimestamp(UUID)` compiles from Java.
- `UUIDv7.extractInstant(UUID)` compiles from Java.

Acceptance criteria:

- Java interop is proven by Java compilation, not reflection only.

### Null Contract

Add explicit Java-facing null tests:

- `parse(null)` throws `NullPointerException`.
- `isValid(null)` throws `NullPointerException` or returns `false`; choose and document one behavior before implementing.
- `isVersion7(null)` throws `NullPointerException`.
- `extractUnixTimestamp(null)` throws `NullPointerException`.
- `extractInstant(null)` throws `NullPointerException`.

Recommended behavior:

- Throw `NullPointerException` for null values because Kotlin non-null parameters already imply that contract.
- Do not silently return `false` for null unless a nullable API is intentionally added.

### Stronger Concurrency

Extend concurrent tests:

- Generate IDs from multiple threads.
- Assert no duplicates.
- Group by extracted timestamp.
- Within each timestamp group, assert `rand_a` values are unique.
- For each timestamp group, sorting by UUID should match sorting by `rand_a`.

Acceptance criteria:

- Test validates the atomic packed-state design under contention.
- Test stays bounded with `@Timeout`.

## Benchmark Boundary

Do not add JMH in this hardening pass unless explicitly requested.

Reason:

- JMH adds build complexity and runtime.
- Unit tests should validate correctness.
- Throughput and allocation claims need a separate benchmark module or separate benchmark source set.

Future benchmark plan:

- Compare current implementation against a naive `SecureRandom` + `ByteArray` + `ByteBuffer` implementation.
- Measure throughput.
- Measure allocation with GC profiler.
- Only then document claims like `50x` throughput or `32 B per call`.

## Execution Order

1. Add Java interop test.
2. Add RFC bit-layout helper functions and tests.
3. Add repeated invariant tests with timeouts.
4. Replace or strengthen same-ms tests.
5. Add entropy variation test.
6. Add null-contract tests.
7. Add stronger concurrency grouping test.
8. Run default check.
9. Run Java 17 and 21 matrix checks locally.
10. Leave Java 8 and 11 checks to CI unless local toolchains are installed.
