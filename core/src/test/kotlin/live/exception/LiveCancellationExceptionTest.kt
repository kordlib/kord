package live.exception

import dev.kord.core.Kord
import dev.kord.core.event.Event
import dev.kord.core.live.exception.LiveCancellationException
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LiveCancellationExceptionTest {

    inner class EventMock : Event {
        override val kord: Kord
            get() = error("Never called")
        override val shard: Int
            get() = error("Never called")
    }

    @Test
    fun `Throw exception without message`() {
        assertFailsWith<LiveCancellationException> {
            throw LiveCancellationException(EventMock(), null)
        }
    }

    @Test
    @Throws(LiveCancellationException::class)
    fun `Throw exception with message`() {
        assertFailsWith<LiveCancellationException> {
            throw LiveCancellationException(EventMock(), "A reason")
        }
    }
}