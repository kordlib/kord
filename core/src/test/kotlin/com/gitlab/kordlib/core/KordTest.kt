package com.gitlab.kordlib.core

import com.gitlab.kordlib.common.annotation.KordExperimental
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import org.junit.jupiter.api.Test
import java.util.concurrent.CountDownLatch

internal class KordTest {

    @Test
    @OptIn(KordExperimental::class)
    fun `Kord life cycle is correctly ended on shutdown`() {
        val kord = Kord.restOnly(System.getenv("token"))
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
