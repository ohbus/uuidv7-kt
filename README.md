# uuidv7-kt

Fast Kotlin/JVM UUIDv7 generator and utilities following RFC 9562.

## Features

- Kotlin implementation with Java-friendly static APIs.
- Java 8-compatible bytecode.
- Runtime dependency limited to `kotlin-stdlib`.
- Per-JVM monotonic UUIDv7 generation.
- Strict UUIDv7 parsing and validation.
- Timestamp extraction helpers.

## Install

```kotlin
repositories {
	maven {
		url = uri("https://maven.pkg.github.com/ohbus/uuidv7-kt")
		credentials {
			username = providers.environmentVariable("GITHUB_ACTOR").orNull
			password = providers.environmentVariable("GITHUB_TOKEN").orNull
		}
	}
}

dependencies {
	implementation("com.subhrodip:uuidv7-kt:0.1.0")
}
```

## Use

Kotlin:

```kotlin
val id = UUIDv7.generate()
val timestamp = UUIDv7.extractInstant(id)
```

Java:

```java
UUID id = UUIDv7.generate();
Instant timestamp = UUIDv7.extractInstant(id);
```

## Throughput And Security

The generator uses `ThreadLocalRandom` for high-throughput identifier generation. UUIDs produced by this library are not suitable as secrets, session tokens, password reset tokens, or authorization credentials.

Generation is monotonic inside a single JVM process. It does not provide distributed ordering across hosts or processes.

## License

MIT
