package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.core.live.LiveUser
import dev.kord.core.live.live
import dev.kord.core.live.onUpdate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.BeforeTest
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class LiveUserTest : AbstractLiveEntityTest<LiveUser>() {

    @BeforeTest
    fun onBefore() = runBlocking {
        live = kord.getSelf().live()
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                countDown()
            }

            kord.editSelf {
                this.username = "RENAME_TEST_LIVE_USER"
            }
        }
    }
}