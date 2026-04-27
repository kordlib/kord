package dev.kord.voice.dave

import kotlin.test.*
import org.junit.jupiter.api.Nested

class NoOpDaveProtocolTest {

    @Nested
    inner class Properties {
        @Test
        fun `isActive returns false`() {
            assertFalse(NoOpDaveProtocol.isActive)
        }

        @Test
        fun `maxProtocolVersion returns 0`() {
            assertEquals(0, NoOpDaveProtocol.maxProtocolVersion)
        }

        @Test
        fun `currentProtocolVersion returns 0`() {
            assertEquals(0, NoOpDaveProtocol.currentProtocolVersion)
        }
    }

    @Nested
    inner class FramePassthrough {
        @Test
        fun `encryptFrame returns input unchanged`() {
            val frame = byteArrayOf(1, 2, 3, 4, 5)
            val result = NoOpDaveProtocol.encryptFrame(42u, frame)
            assertSame(frame, result)
        }

        @Test
        fun `decryptFrame returns input unchanged`() {
            val frame = byteArrayOf(10, 20, 30)
            val result = NoOpDaveProtocol.decryptFrame(123L, frame)
            assertSame(frame, result)
        }

        @Test
        fun `encryptFrame with empty array returns same empty array`() {
            val frame = byteArrayOf()
            val result = NoOpDaveProtocol.encryptFrame(0u, frame)
            assertSame(frame, result)
        }

        @Test
        fun `decryptFrame with empty array returns same empty array`() {
            val frame = byteArrayOf()
            val result = NoOpDaveProtocol.decryptFrame(0L, frame)
            assertSame(frame, result)
        }
    }

    @Nested
    inner class MlsOperations {
        @Test
        fun `processCommit returns Ignored`() {
            val result = NoOpDaveProtocol.processCommit(byteArrayOf(1, 2, 3))
            assertSame(DaveCommitResult.Ignored, result)
        }

        @Test
        fun `processProposals returns null`() {
            val result = NoOpDaveProtocol.processProposals(byteArrayOf(1), setOf(1L))
            assertNull(result)
        }

        @Test
        fun `processWelcome returns null`() {
            val result = NoOpDaveProtocol.processWelcome(byteArrayOf(1), setOf(1L))
            assertNull(result)
        }

        @Test
        fun `getMarshalledKeyPackage returns empty byte array`() {
            val result = NoOpDaveProtocol.getMarshalledKeyPackage()
            assertNotNull(result)
            assertEquals(0, result.size)
        }

        @Test
        fun `getLastEpochAuthenticator returns empty byte array`() {
            val result = NoOpDaveProtocol.getLastEpochAuthenticator()
            assertEquals(0, result.size)
        }
    }

    @Nested
    inner class LifecycleOperations {
        @Test
        fun `close does not throw`() {
            NoOpDaveProtocol.close()
        }

        @Test
        fun `initialize does not throw`() {
            NoOpDaveProtocol.initialize(1, "channel123", 456L, "session789")
        }

        @Test
        fun `reset does not throw`() {
            NoOpDaveProtocol.reset()
        }

        @Test
        fun `setExternalSender does not throw`() {
            NoOpDaveProtocol.setExternalSender(byteArrayOf(1, 2, 3))
        }

        @Test
        fun `prepareKeyRatchets does not throw`() {
            NoOpDaveProtocol.prepareKeyRatchets(1, 1)
        }

        @Test
        fun `executeTransition does not throw`() {
            NoOpDaveProtocol.executeTransition(1)
        }

        @Test
        fun `addUser does not throw`() {
            NoOpDaveProtocol.addUser(123L)
        }

        @Test
        fun `removeUser does not throw`() {
            NoOpDaveProtocol.removeUser(123L)
        }

        @Test
        fun `assignSsrcToCodec does not throw`() {
            NoOpDaveProtocol.assignSsrcToCodec(42u)
        }

        @Test
        fun `setProtocolVersion does not throw`() {
            NoOpDaveProtocol.setProtocolVersion(1)
        }
    }
}
