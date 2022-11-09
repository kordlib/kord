package live

import BoxedSnowflake
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.live.LiveUser
import dev.kord.core.live.onUpdate
import dev.kord.gateway.UserUpdate
import equality.randomId
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveUserTest : AbstractLiveEntityTest<LiveUser>() {

    private lateinit var userId: BoxedSnowflake

    @BeforeAll
    override fun onBeforeAll() {
        super.onBeforeAll()
        userId = BoxedSnowflake(randomId())
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveUser(
            user = User(
                kord = kord,
                data = UserData(
                    id = userId.value,
                    username = "",
                    discriminator = ""
                )
            )
        )
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(it.user.id, userId.value)
                count()
            }

            sendEventValidAndRandomId(userId.value) {
                UserUpdate(
                    DiscordUser(
                        id = it,
                        username = "",
                        discriminator = "",
                        avatar = null
                    ),
                    0
                )
            }
        }
    }
}
