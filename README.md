# uuidv7-kt

[![Maven Central](https://img.shields.io/maven-central/v/com.subhrodip/uuidv7-kt?label=Maven%20Central)](https://central.sonatype.com/artifact/com.subhrodip/uuidv7-kt)
[![Java 8+](https://img.shields.io/badge/Java-8%2B-blue)](https://github.com/ohbus/uuidv7-kt)
[![License: MIT](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

Fast Kotlin/JVM UUIDv7 generator and utilities following RFC 9562.

## Install

`uuidv7-kt` is published to Maven Central as:

```text
com.subhrodip:uuidv7-kt
```

Gradle Kotlin DSL:

```kotlin
repositories {
	mavenCentral()
}

dependencies {
	implementation("com.subhrodip:uuidv7-kt:0.0.1")
}
```

Gradle Groovy DSL:

```groovy
repositories {
	mavenCentral()
}

dependencies {
	implementation 'com.subhrodip:uuidv7-kt:0.0.1'
}
```

Maven:

```xml
<dependency>
	<groupId>com.subhrodip</groupId>
	<artifactId>uuidv7-kt</artifactId>
	<version>0.0.1</version>
</dependency>
```

## Usage

Kotlin:

```kotlin
import com.subhrodip.uuidv7.UUIDv7

val id = UUIDv7.generate()
val sameAsGenerate = UUIDv7.randomUUID()

val parsed = UUIDv7.parse(id.toString())
val valid = UUIDv7.isValid(id.toString())
val timestampMillis = UUIDv7.extractUnixTimestamp(id)
val timestamp = UUIDv7.extractInstant(id)
```

Java:

```java
import com.subhrodip.uuidv7.UUIDv7;

import java.time.Instant;
import java.util.UUID;

UUID id = UUIDv7.generate();
UUID sameAsGenerate = UUIDv7.randomUUID();

UUID parsed = UUIDv7.parse(id.toString());
boolean valid = UUIDv7.isValid(id.toString());
long timestampMillis = UUIDv7.extractUnixTimestamp(id);
Instant timestamp = UUIDv7.extractInstant(id);
```

## Features

- Kotlin implementation with Java-friendly static APIs.
- Java 8-compatible bytecode.
- Runtime dependency limited to `kotlin-stdlib`.
- Per-JVM monotonic UUIDv7 generation.
- Strict UUIDv7 parsing and validation.
- Timestamp extraction helpers.

## Throughput And Security

The generator uses `ThreadLocalRandom` for high-throughput identifier generation. UUIDs produced by this library are not suitable as secrets, session tokens, password reset tokens, or authorization credentials.

Generation is monotonic inside a single JVM process. It does not provide distributed ordering across hosts or processes.

## License

MIT
