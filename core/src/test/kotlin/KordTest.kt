package dev.kord.core

import dev.kord.common.annotation.KordExperimental
import dev.kord.core.event.gateway.ReadyEvent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.concurrent.CountDownLatch
import kotlin.test.assertEquals

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
internal class KordTest {

    @Test
    @OptIn(KordExperimental::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
    fun `Kord life cycle is correctly ended on shutdown`() = runBlocking {
        val kord = Kord.restOnly(System.getenv("KORD_TEST_TOKEN"))
        val job = kord.on<ReadyEvent> {}
        kord.shutdown()
        assertEquals(false, job.isActive)

    }
}

