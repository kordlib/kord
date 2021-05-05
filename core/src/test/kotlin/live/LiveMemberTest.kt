package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Member
import dev.kord.core.live.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
class LiveMemberTest : AbstractLiveEntityTest<LiveMember>() {

    private lateinit var member: Member

    @BeforeAll
    override fun onBeforeAll() = runBlocking {
        super.onBeforeAll()
        guild = createGuild()
        member = createMember()
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = member.live()
    }

    private suspend fun createMember(): Member {
        val guildId = requireGuild().id
        return kord.getSelf().asMember(guildId)
    }

    @Test
    fun `Check onUpdate is called when event is received`() {
        countdownContext(1) {
            live.onUpdate {
                countDown()
            }

            member.edit {
                val role = requireGuild().createRole {
                    name = "ROLE_TEST_LIVE_MEMBER"
                }
                roles = mutableSetOf(role.id)
            }
        }
    }

    @Ignore
    @Test
    fun `Check onLeave is called when event is received`() {
        countdownContext(1) {
            live.onLeave {
                countDown()
            }

            // Need an other member
            member.kick("LEAVE_TEST_LIVE_MEMBER")
        }
    }

    @Ignore
    @Test
    fun `Check onShutdown is called when the member is banned`() {
        countdownContext(1) {
            live.onShutdown {
                countDown()
            }

            // Need an other member
            member.ban {
                this.reason = "BAN_TEST_LIVE_MEMBER"
            }
        }
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
        member = createMember()
    }
}