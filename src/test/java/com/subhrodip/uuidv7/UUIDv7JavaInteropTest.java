package com.subhrodip.uuidv7;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UUIDv7JavaInteropTest {
    @Test
    void javaCallersCanUseStaticApi() {
        UUID generated = UUIDv7.generate();
        UUID alias = UUIDv7.randomUUID();
        UUID parsed = UUIDv7.parse(generated.toString());
        boolean valid = UUIDv7.isValid(generated.toString());
        boolean version7 = UUIDv7.isVersion7(generated);
        long timestamp = UUIDv7.extractUnixTimestamp(generated);
        Instant instant = UUIDv7.extractInstant(generated);

        assertEquals(generated, parsed);
        assertTrue(valid);
        assertTrue(version7);
        assertEquals(timestamp, instant.toEpochMilli());
        assertEquals(7, alias.version());
    }
}
