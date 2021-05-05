package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.live.*
import dev.kord.core.rest.imageBinary
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
class LiveGuildTest : AbstractLiveEntityTest<LiveGuild>() {

    private lateinit var category: Category

    private lateinit var channel: TextChannel

    @BeforeAll
    override fun onBeforeAll() = runBlocking {
        super.onBeforeAll()
        guild = createGuild()
        category = createCategory(requireGuild())
        channel = createTextChannel(category)
    }

    @BeforeTest
    fun onBefore() = runBlocking {
        live = requireGuild().live()
    }

    @Test
    fun `Check onEmojisUpdate is called when event is received`() = runBlocking {
        countdownContext(2) {
            live.onEmojisUpdate {
                countDown()
            }

            // Call event
            val guildEmoji = requireGuild().createEmoji("kord", imageBinary("images/kord.png"))
            // Call event
            guildEmoji.edit {
                name = "kordimg"
            }
        }
    }

    @Test
    fun `Check onBanAdd is called when event is received`() = runBlocking {
        countdownContext(1) {
            live.onBanAdd {
                countDown()
            }

            val userId = Snowflake("242043299022635020")
            requireGuild().ban(userId) {
                this.reason = "BAN_TEST_LIVE_GUILD"
            }
        }
    }

    @Test
    fun `Check onBanRemove is called when event is received`() = runBlocking {
        countdownContext(1) {
            live.onBanRemove {
                countDown()
            }

            val userId = Snowflake("242043299022635020")
            requireGuild().ban(userId) {
                this.reason = "BAN_TEST_LIVE_GUILD"
            }

            requireGuild().unban(userId)
        }
    }

    @Ignore
    @Test
    fun `Check onPresenceUpdate is called when event is received`() = runBlocking {
        countdownContext(1) {
            live.onPresenceUpdate {
                countDown()
            }

            kord.editPresence {
                this.playing("PRESENCE_TEST_LIVE_GUILD")
            }
        }
    }

    @Test
    fun `Check onVoiceServerUpdate is called when event is received`() = runBlocking {
        countdownContext(2) {
            live.onVoiceServerUpdate {
                countDown()
            }

            val voiceChannel = createVoiceChannel(category)
            voiceChannel.edit {
                this.userLimit = 1
            }
        }
    }

    @Ignore
    @Test
    fun `Check VoiceStateUpdateEvent is called when event is received`() = runBlocking {
        countdownContext(2) {
            live.onVoiceStateUpdate {
                countDown()
            }

            val voiceChannel = createVoiceChannel(category)
            voiceChannel.edit {
                this.userLimit = 1
            }
        }
    }
}