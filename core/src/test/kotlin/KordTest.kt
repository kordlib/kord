package dev.kord.core

import dev.kord.common.annotation.KordExperimental
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import java.util.concurrent.CountDownLatch

@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
internal class KordTest {

    @Test
    @OptIn(KordExperimental::class)
    fun `Kord life cycle is correctly ended on shutdown`() {
        val kord = Kord.restOnly(System.getenv("KORD_TEST_TOKEN"))
        val lock = CountDownLatch(1)
        kord.events.onCompletion { lock.countDown() }.launchIn(kord)

        runBlocking {
            kord.shutdown()

            withTimeout(1000) {
                //put blocking call on separate thread.
                GlobalScope.launch(Dispatchers.IO) { lock.await() }.join()
            }
        }
    }

}
