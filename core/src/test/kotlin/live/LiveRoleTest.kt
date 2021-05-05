package live

import dev.kord.common.Color
import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.createRole
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Role
import dev.kord.core.live.LiveRole
import dev.kord.core.live.live
import dev.kord.core.live.onUpdate
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import kotlin.test.BeforeTest
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
@EnabledIfEnvironmentVariable(named = "KORD_TEST_TOKEN", matches = ".+")
class LiveRoleTest : AbstractLiveEntityTest<LiveRole>() {

    lateinit var role: Role

    @BeforeAll
    override fun onBeforeAll() = runBlocking {
        super.onBeforeAll()
        guild = createGuild()
        role = createRole()
    }

    @BeforeTest
    fun onBefore() {
        live = role.live()
    }

    private suspend fun createRole(): Role = requireGuild().createRole {
        name = "ROLE_TEST_LIVE_ROLE"
        color = Color(0)
        mentionable = true
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                countDown()
            }

            role.edit {
                color = Color(255, 255, 255)
            }
        }
    }

    @Test
    fun `Check onShutdown is called when the role is deleted`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            role.delete()
        }
        role = createRole()
    }

    @Test
    fun `Check onShutdown is called when the guild is deleted`() = runBlocking {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }
            requireGuild().delete()
        }
        guild = createGuild()
        role = createRole()
    }
}