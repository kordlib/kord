package dev.kord.core.live

import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.UserData
import dev.kord.core.entity.User
import dev.kord.core.randomId
import dev.kord.gateway.UserUpdate
import kotlinx.coroutines.test.runTest
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Ignore
class LiveUserTest : AbstractLiveEntityTest<LiveUser>() {

    private val userId: Snowflake = randomId()

    @BeforeTest
    fun onBefore() = runTest {
        live = LiveUser(
            user = User(
                kord = kord,
                data = UserData(
                    id = userId,
                    username = "",
                    discriminator = ""
                )
            )
        )
    }

    @Test
    @JsName("test1")
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                assertEquals(it.user.id, userId)
                count()
            }

            sendEventValidAndRandomId(userId) {
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
