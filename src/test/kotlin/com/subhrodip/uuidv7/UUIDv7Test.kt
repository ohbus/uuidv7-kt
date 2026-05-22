package com.subhrodip.uuidv7

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Timeout
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
	fun `generated timestamp is recent and masked to 48 bits`() {
		val before = System.currentTimeMillis() and TIMESTAMP_MASK
		val uuid = UUIDv7.generate()
		val after = System.currentTimeMillis() and TIMESTAMP_MASK

		val timestamp = UUIDv7.extractUnixTimestamp(uuid)

		assertTrue(timestamp in before..after)
		assertEquals(timestamp, extractTimestamp(uuid))
	}

	@Test
	fun `extractInstant returns embedded timestamp`() {
		val uuid = UUIDv7.generate()

		assertEquals(UUIDv7.extractUnixTimestamp(uuid), UUIDv7.extractInstant(uuid).toEpochMilli())
	}

	@Test
	fun `RFC 9562 UUIDv7 bit layout is encoded directly`() {
		val uuid = UUIDv7.generate()

		assertEquals(UUIDv7.extractUnixTimestamp(uuid), uuid.mostSignificantBits ushr TIMESTAMP_SHIFT)
		assertEquals(7, extractVersionNibble(uuid))
		assertTrue(extractRandA(uuid) in 0..SEQUENCE_MASK.toInt())
		assertEquals(VARIANT_BITS, uuid.leastSignificantBits and VARIANT_MASK)
		assertEquals(uuid.leastSignificantBits and RAND_B_MASK, extractRandB(uuid))
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
	fun `Java nulls fail according to Kotlin non-null contract`() {
		assertThrows(NullPointerException::class.java) {
			UUIDv7.parse(null!!)
		}
		assertThrows(NullPointerException::class.java) {
			UUIDv7.isValid(null!!)
		}
		assertThrows(NullPointerException::class.java) {
			UUIDv7.isVersion7(null!!)
		}
		assertThrows(NullPointerException::class.java) {
			UUIDv7.extractUnixTimestamp(null!!)
		}
		assertThrows(NullPointerException::class.java) {
			UUIDv7.extractInstant(null!!)
		}
	}

	@Test
	@Timeout(2)
	fun `no duplicate UUIDs are generated`() {
		val uuids = HashSet<UUID>(100_000)

		repeat(100_000) {
			assertTrue(uuids.add(UUIDv7.generate()))
		}
	}

	@Test
	@Timeout(1)
	fun `version and variant stay constant over many generations`() {
		repeat(10_000) {
			val uuid = UUIDv7.generate()
			assertEquals(7, uuid.version())
			assertEquals(2, uuid.variant())
		}
	}

	@Test
	@Timeout(1)
	fun `extracted timestamps are non-decreasing over many generations`() {
		var previousTimestamp = -1L

		repeat(1_000) {
			val timestamp = UUIDv7.extractUnixTimestamp(UUIDv7.generate())
			assertTrue(timestamp >= previousTimestamp)
			previousTimestamp = timestamp
		}
	}

	@Test
	@Timeout(1)
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
	@Timeout(1)
	fun `UUIDs generated within same millisecond are monotonic`() {
		val window = sameMillisecondWindow(minimumSize = 5)

		val sequences = window.map(::extractRandA)
		for (index in 1 until sequences.size) {
			assertTrue(sequences[index] > sequences[index - 1])
			assertTrue(window[index - 1] < window[index])
		}
	}

	@RepeatedTest(50)
	@Timeout(1)
	fun `same millisecond pairs are distinct and monotonic when clock does not tick`() {
		waitForFreshMillisecond()

		val first = UUIDv7.generate()
		val second = UUIDv7.generate()
		val firstTimestamp = UUIDv7.extractUnixTimestamp(first)
		val secondTimestamp = UUIDv7.extractUnixTimestamp(second)

		if (firstTimestamp == secondTimestamp) {
			assertNotEquals(first, second)
			assertTrue(extractRandA(second) > extractRandA(first))
			assertTrue(first < second)
		} else {
			assertTrue(secondTimestamp > firstTimestamp)
		}
	}

	@Test
	@Timeout(2)
	fun `rand_b varies for same millisecond UUIDs`() {
		val pair = findSameMillisecondPair()
		assertNotEquals(extractRandB(pair.first), extractRandB(pair.second))
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
	@Timeout(5)
	fun `concurrent generation creates no duplicates and preserves same timestamp ordering`() {
		val uuids = generateConcurrently(total = 12_000)

		assertEquals(uuids.size, uuids.toSet().size)

		val byTimestamp = uuids.groupBy(UUIDv7::extractUnixTimestamp)
		for ((timestamp, group) in byTimestamp) {
			if (group.size <= 1) {
				continue
			}

			val withSequence = group.map { it to extractRandA(it) }
			val sequences = withSequence.map { it.second }

			assertEquals(sequences.size, sequences.toSet().size, "Duplicate rand_a within timestamp $timestamp")
			assertEquals(withSequence.sortedBy { it.second }.map { it.first }, group.sorted())
		}
	}

	private fun generateConcurrently(total: Int): List<UUID> {
		val threads = Runtime.getRuntime().availableProcessors().coerceAtLeast(4)
		val basePerThread = total / threads
		val remainder = total % threads
		val pool = Executors.newFixedThreadPool(threads)
		val start = CountDownLatch(1)
		val done = CountDownLatch(threads)
		val uuids = Collections.synchronizedList(mutableListOf<UUID>())

		repeat(threads) { threadIndex ->
			pool.execute {
				start.await()
				val count = basePerThread + if (threadIndex < remainder) 1 else 0
				repeat(count) {
					uuids += UUIDv7.generate()
				}
				done.countDown()
			}
		}

		start.countDown()
		assertTrue(done.await(30, TimeUnit.SECONDS))
		pool.shutdownNow()
		return uuids
	}

	private fun sameMillisecondWindow(minimumSize: Int): List<UUID> {
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
				window.size >= minimumSize -> return window
				else -> {
					window.clear()
					window.add(uuid)
					timestamp = currentTimestamp
				}
			}
		}

		assertTrue(window.size >= minimumSize)
		return window
	}

	private fun findSameMillisecondPair(): Pair<UUID, UUID> {
		repeat(1_000) {
			waitForFreshMillisecond()
			val first = UUIDv7.generate()
			val second = UUIDv7.generate()
			if (UUIDv7.extractUnixTimestamp(first) == UUIDv7.extractUnixTimestamp(second)) {
				return first to second
			}
		}

		error("Failed to capture same-millisecond pair")
	}

	private fun waitForFreshMillisecond() {
		val now = System.currentTimeMillis()
		while (System.currentTimeMillis() == now) {
			Thread.yield()
		}
	}

	private fun extractTimestamp(uuid: UUID): Long = (uuid.mostSignificantBits ushr TIMESTAMP_SHIFT) and TIMESTAMP_MASK

	private fun extractVersionNibble(uuid: UUID): Int =
		((uuid.mostSignificantBits ushr SEQUENCE_BITS) and VERSION_MASK).toInt()

	private fun extractRandA(uuid: UUID): Int = (uuid.mostSignificantBits and SEQUENCE_MASK).toInt()

	private fun extractRandB(uuid: UUID): Long = uuid.leastSignificantBits and RAND_B_MASK

	private fun lastState(): AtomicLong {
		val field = UUIDv7::class.java.getDeclaredField("lastState")
		field.isAccessible = true
		return field.get(null) as AtomicLong
	}

	private companion object {
		private const val SEQUENCE_BITS = 12
		private const val TIMESTAMP_SHIFT = 16
		private const val VERSION_MASK = 0x0FL
		private const val SEQUENCE_MASK = 0x0FFFL
		private const val TIMESTAMP_MASK = 0xFFFFFFFFFFFFL
		private const val VARIANT_MASK = -0x4000000000000000L
		private const val VARIANT_BITS = Long.MIN_VALUE
		private const val RAND_B_MASK = 0x3FFFFFFFFFFFFFFFL
	}
}
