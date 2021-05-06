package live

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.core.behavior.createEmoji
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.channel.Category
import dev.kord.core.entity.channel.TextChannel
import dev.kord.core.live.*
import dev.kord.core.rest.imageBinary
import dev.kord.gateway.GuildEmojisUpdate
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import kotlin.test.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordExperimental::class, KordPreview::class)
class LiveGuildTest : AbstractLiveEntityTest<LiveGuild>() {

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveGuild(
            Guild(
                kord = kord,
                data = GuildData(
                    id = guildId,
                    name = "",
                    ownerId = randomId(),
                    region = "",
                    afkTimeout = 0,
                    verificationLevel = VerificationLevel.None,
                    defaultMessageNotifications = DefaultMessageNotificationLevel.AllMessages,
                    explicitContentFilter = ExplicitContentFilter.Disabled,
                    roles = emptyList(),
                    emojis = emptyList(),
                    features = emptyList(),
                    mfaLevel = MFALevel.None,
                    premiumTier = PremiumTier.None,
                    preferredLocale = "",
                    systemChannelFlags = SystemChannelFlags(0)
                )
            )
        )
    }

    @Test
    fun `Check onEmojisUpdate is called when event is received`() = runBlocking {
        countdownContext(1) {
            live.onEmojisUpdate {
                assertEquals(guildId, it.guildId)
                countDown()
            }

            fun createEvent(guildId: Snowflake) = GuildEmojisUpdate(
                DiscordUpdatedEmojis(
                    guildId = guildId,
                    emojis = emptyList()
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId())
            sendEvent(eventRandomGuild)

            val event = createEvent(guildId)
            sendEvent(event)
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