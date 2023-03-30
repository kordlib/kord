package dev.kord.core

import dev.kord.core.event.gateway.ReadyEvent
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.Test
import kotlin.test.assertEquals

internal class KordTest {
    @Test
    @JsName("test1")
    fun `Kord life cycle is correctly ended on shutdown`() = runTest {
        val kord = Kord.restOnly(testToken)
        val job = kord.on<ReadyEvent> {}
        kord.shutdown()
        assertEquals(false, job.isActive)
    }
}
