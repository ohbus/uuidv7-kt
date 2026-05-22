package com.subhrodip.uuidv7

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Collections
import java.util.UUID
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class UUIDv7Test {
	@AfterEach
	fun resetState() {
		lastState().set(0)
	}

	@Test
	fun `generate returns UUIDv7 with IETF variant`() {
		val uuid = UUIDv7.generate()

		assertEquals(7, uuid.version())
		assertEquals(2, uuid.variant())
	}

	@Test
	fun `randomUUID delegates to generate contract`() {
		val uuid = UUIDv7.randomUUID()

		assertEquals(7, uuid.version())
		assertEquals(2, uuid.variant())
	}

	@Test
	fun `generated timestamp is recent`() {
		val before = System.currentTimeMillis()
		val uuid = UUIDv7.generate()
		val after = System.currentTimeMillis()

		val timestamp = UUIDv7.extractUnixTimestamp(uuid)

		assertTrue(timestamp in before..after)
	}

	@Test
	fun `extractInstant returns embedded timestamp`() {
		val uuid = UUIDv7.generate()

		assertEquals(UUIDv7.extractUnixTimestamp(uuid), UUIDv7.extractInstant(uuid).toEpochMilli())
	}

	@Test
	fun `parse accepts UUIDv7 string`() {
		val uuid = UUIDv7.generate()

		assertEquals(uuid, UUIDv7.parse(uuid.toString()))
	}

	@Test
	fun `parse rejects non UUIDv7 string`() {
		val uuid = UUID.randomUUID()

		assertThrows(IllegalArgumentException::class.java) {
			UUIDv7.parse(uuid.toString())
		}
	}

	@Test
	fun `parse rejects malformed string`() {
		assertThrows(IllegalArgumentException::class.java) {
			UUIDv7.parse("not-a-uuid")
		}
	}

	@Test
	fun `isValid is strict UUIDv7 validation`() {
		assertTrue(UUIDv7.isValid(UUIDv7.generate().toString()))
		assertFalse(UUIDv7.isValid(UUID.randomUUID().toString()))
		assertFalse(UUIDv7.isValid("not-a-uuid"))
	}

	@Test
	fun `isVersion7 rejects other UUID versions`() {
		assertTrue(UUIDv7.isVersion7(UUIDv7.generate()))
		assertFalse(UUIDv7.isVersion7(UUID.randomUUID()))
	}

	@Test
	fun `timestamp extraction rejects non UUIDv7`() {
		assertThrows(IllegalArgumentException::class.java) {
			UUIDv7.extractUnixTimestamp(UUID.randomUUID())
		}
	}

	@Test
	fun `no duplicate UUIDs are generated`() {
		val uuids = HashSet<UUID>()

		repeat(100_000) {
			assertTrue(uuids.add(UUIDv7.generate()))
		}
	}

	@Test
	fun `UUIDs generated across milliseconds are ordered`() {
		val first = UUIDv7.generate()
		val firstTimestamp = UUIDv7.extractUnixTimestamp(first)

		var second: UUID
		do {
			second = UUIDv7.generate()
		} while (UUIDv7.extractUnixTimestamp(second) == firstTimestamp)

		assertTrue(first < second)
	}

	@Test
	fun `UUIDs generated within same millisecond are monotonic`() {
		val window = mutableListOf<UUID>()
		var timestamp = -1L

		repeat(50_000) {
			val uuid = UUIDv7.generate()
			val currentTimestamp = UUIDv7.extractUnixTimestamp(uuid)
			when {
				window.isEmpty() -> {
					window.add(uuid)
					timestamp = currentTimestamp
				}
				currentTimestamp == timestamp -> window.add(uuid)
				window.size >= 5 -> return@repeat
				else -> {
					window.clear()
					window.add(uuid)
					timestamp = currentTimestamp
				}
			}
		}

		assertTrue(window.size >= 5)

		val sequences = window.map(::extractRandA)
		for (index in 1 until sequences.size) {
			assertTrue(sequences[index] > sequences[index - 1])
			assertTrue(window[index - 1] < window[index])
		}
	}

	@Test
	fun `clock regression uses previous timestamp and increments sequence`() {
		val uuid = UUIDv7.generate()
		val futureTimestamp = UUIDv7.extractUnixTimestamp(uuid) + 1_000
		val futureSequence = 100L
		lastState().set((futureTimestamp shl SEQUENCE_BITS) or futureSequence)

		val regressed = UUIDv7.generate()

		assertEquals(futureTimestamp, UUIDv7.extractUnixTimestamp(regressed))
		assertEquals(futureSequence + 1, extractRandA(regressed).toLong())
		assertTrue(uuid < regressed)
	}

	@Test
	fun `sequence overflow increments logical timestamp and resets sequence`() {
		val timestamp = System.currentTimeMillis()
		lastState().set((timestamp shl SEQUENCE_BITS) or SEQUENCE_MASK)

		val uuid = UUIDv7.generate()

		assertEquals(timestamp + 1, UUIDv7.extractUnixTimestamp(uuid))
		assertEquals(0, extractRandA(uuid))
	}

	@Test
	fun `concurrent generation creates no duplicates`() {
		val threads = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)
		val perThread = 12_000 / threads
		val pool = Executors.newFixedThreadPool(threads)
		val start = CountDownLatch(1)
		val done = CountDownLatch(threads)
		val uuids = Collections.synchronizedList(mutableListOf<UUID>())

		repeat(threads) {
			pool.execute {
				start.await()
				repeat(perThread) {
					uuids += UUIDv7.generate()
				}
				done.countDown()
			}
		}

		start.countDown()
		assertTrue(done.await(30, TimeUnit.SECONDS))
		pool.shutdownNow()

		assertEquals(uuids.size, uuids.toSet().size)
	}

	@Test
	fun `strict parse is Java friendly`() {
		assertDoesNotThrow {
			UUIDv7::class.java.getMethod("generate")
			UUIDv7::class.java.getMethod("randomUUID")
			UUIDv7::class.java.getMethod("parse", String::class.java)
		}
	}

	private fun extractRandA(uuid: UUID): Int = (uuid.mostSignificantBits and SEQUENCE_MASK).toInt()

	private fun lastState(): AtomicLong {
		val field = UUIDv7::class.java.getDeclaredField("lastState")
		field.isAccessible = true
		return field.get(null) as AtomicLong
	}

	private companion object {
		private const val SEQUENCE_BITS = 12
		private const val SEQUENCE_MASK = 0x0FFFL
	}
}
