package dev.kord.voice.dave

import kotlin.test.*
import org.junit.jupiter.api.Nested

class DaveCommitResultTest {

    @Nested
    inner class Singletons {
        @Test
        fun `Failed is a singleton`() {
            val a = DaveCommitResult.Failed
            val b = DaveCommitResult.Failed
            assertSame(a, b)
        }

        @Test
        fun `Ignored is a singleton`() {
            val a = DaveCommitResult.Ignored
            val b = DaveCommitResult.Ignored
            assertSame(a, b)
        }

        @Test
        fun `Failed and Ignored are different instances`() {
            assertNotSame<DaveCommitResult>(DaveCommitResult.Failed, DaveCommitResult.Ignored)
        }
    }

    @Nested
    inner class SuccessVariant {
        @Test
        fun `Success contains a rosterMap`() {
            val roster = mapOf(1L to byteArrayOf(1, 2), 2L to byteArrayOf(3, 4))
            val success = DaveCommitResult.Success(roster)
            assertSame(roster, success.rosterMap)
        }

        @Test
        fun `Success with empty roster map`() {
            val success = DaveCommitResult.Success(emptyMap())
            assertTrue(success.rosterMap.isEmpty())
        }

        @Test
        fun `two Success instances with same map are equal`() {
            val map = mapOf(1L to byteArrayOf(1))
            val a = DaveCommitResult.Success(map)
            val b = DaveCommitResult.Success(map)
            assertEquals(a, b)
        }
    }

    @Nested
    inner class PatternMatching {
        @Test
        fun `when expression matches Failed`() {
            val result: DaveCommitResult = DaveCommitResult.Failed
            val matched = when (result) {
                is DaveCommitResult.Failed -> "failed"
                is DaveCommitResult.Ignored -> "ignored"
                is DaveCommitResult.Success -> "success"
            }
            assertEquals("failed", matched)
        }

        @Test
        fun `when expression matches Ignored`() {
            val result: DaveCommitResult = DaveCommitResult.Ignored
            val matched = when (result) {
                is DaveCommitResult.Failed -> "failed"
                is DaveCommitResult.Ignored -> "ignored"
                is DaveCommitResult.Success -> "success"
            }
            assertEquals("ignored", matched)
        }

        @Test
        fun `when expression matches Success`() {
            val result: DaveCommitResult = DaveCommitResult.Success(mapOf(1L to byteArrayOf(1)))
            val matched = when (result) {
                is DaveCommitResult.Failed -> "failed"
                is DaveCommitResult.Ignored -> "ignored"
                is DaveCommitResult.Success -> "success"
            }
            assertEquals("success", matched)
        }

        @Test
        fun `Success can be destructured to access rosterMap`() {
            val roster = mapOf(42L to byteArrayOf(0xAA.toByte()))
            val result: DaveCommitResult = DaveCommitResult.Success(roster)
            when (result) {
                is DaveCommitResult.Success -> {
                    assertSame(roster, result.rosterMap)
                    assertEquals(1, result.rosterMap.size)
                    assertTrue(42L in result.rosterMap)
                }
                else -> fail("Should match Success")
            }
        }
    }
}
