---
name: Library Implementation Plan
status: Planned
progress:
  - "[x] Create UUIDv7 Kotlin object"
  - "[x] Preserve Java-friendly static API"
  - "[x] Implement UUIDv7 generation"
  - "[x] Implement parse and validation helpers"
  - "[x] Implement timestamp extraction helpers"
  - "[x] Add implementation tests"
---

# Library Implementation Plan

## Goal

Implement a minimal Kotlin UUIDv7 utility in package `com.subhrodip.uuidv7`.

## Public API

The first implementation should expose one public object:

- `UUIDv7.generate(): UUID`
- `UUIDv7.randomUUID(): UUID`
- `UUIDv7.parse(value: String): UUID`
- `UUIDv7.isValid(value: String): Boolean`
- `UUIDv7.isVersion7(uuid: UUID): Boolean`
- `UUIDv7.extractUnixTimestamp(uuid: UUID): Long`
- `UUIDv7.extractInstant(uuid: UUID): Instant`

All public methods should be annotated with `@JvmStatic` so Java consumers can call `UUIDv7.generate()`.

## Implementation Rules

- Use `java.util.UUID`.
- Use `java.time.Instant`.
- Use `AtomicLong` for packed monotonic state.
- Use `ThreadLocalRandom` for fast random bits.
- Do not use `SecureRandom` in v1.
- Do not introduce external runtime dependencies beyond `kotlin-stdlib`.
- Do not add configurable clocks or random providers in v1.

## Behavior

- Follow RFC 9562 UUIDv7 bit layout.
- Store Unix epoch milliseconds in the top 48 bits.
- Set UUID version to 7.
- Set UUID variant to IETF variant 2.
- Use a 12-bit per-JVM monotonic sequence in `rand_a`.
- Use 62 random bits in `rand_b`.
- Guarantee monotonic generation only inside one JVM process.
- Document that UUIDs are not cryptographic tokens.

## Edge Cases

- If multiple UUIDs are generated in the same millisecond, increment the sequence.
- If the system clock moves backward, continue from the last timestamp and increment the sequence.
- If the sequence overflows, increment the logical timestamp and reset the sequence.
- `parse` should reject non-v7 UUID values.
- timestamp extraction should reject non-v7 UUID values.

## Throughput Decision

Use `ThreadLocalRandom` for v1.

Why:

- It is the better default for systems handling very high request rates.
- It avoids contention on a shared random generator.
- It avoids latency spikes caused by secure entropy collection.
- UUIDv7 uniqueness is primarily provided by timestamp, monotonic sequence, and random space, not by cryptographic secrecy.

Tradeoff:

- `ThreadLocalRandom` is not appropriate for secrets, tokens, password reset links, session IDs, or anything where unpredictability is a security boundary.
- If cryptographic unpredictability becomes a real requirement, add a separate API later rather than slowing the default path.

## Parse Contract

`parse(value)` is strict. It should:

- Parse canonical UUID strings.
- Reject malformed UUID strings.
- Reject valid UUID strings that are not UUIDv7.
- Return only UUID values where `version() == 7` and `variant() == 2`.

## Implementation Status

Implemented in `src/main/kotlin/com/subhrodip/uuidv7/UUIDv7.kt`.
