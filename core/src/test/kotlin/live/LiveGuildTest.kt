package live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.live.*
import dev.kord.gateway.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.TestInstance
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@OptIn(KordPreview::class)
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
    fun `Check onEmojisUpdate is called when event is received`() {
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
    fun `Check onBanAdd is called when event is received`() {
        countdownContext(1) {
            live.onBanAdd {
                assertEquals(guildId, it.guildId)
                countDown()
            }

            fun createEvent(guildId: Snowflake) = GuildBanAdd(
                DiscordGuildBan(
                    guildId = guildId.asString,
                    user = DiscordUser(
                        id = randomId(),
                        username = "",
                        discriminator = "",
                        avatar = null
                    )
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
    fun `Check onBanRemove is called when event is received`() {
        countdownContext(1) {
            live.onBanRemove {
                assertEquals(guildId, it.guildId)
                countDown()
            }

            fun createEvent(guildId: Snowflake) = GuildBanRemove(
                DiscordGuildBan(
                    guildId = guildId.asString,
                    user = DiscordUser(
                        id = randomId(),
                        username = "",
                        discriminator = "",
                        avatar = null
                    )
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
    fun `Check onPresenceUpdate is called when event is received`() {
        countdownContext(1) {
            live.onPresenceUpdate {
                assertEquals(guildId, it.guildId)
                countDown()
            }

            fun createEvent(guildId: Snowflake) = PresenceUpdate(
                DiscordPresenceUpdate(
                    user = DiscordPresenceUser(
                        id = randomId(),
                        details = JsonObject(emptyMap())
                    ),
                    guildId = guildId.optionalSnowflake(),
                    status = PresenceStatus.DoNotDisturb,
                    activities = emptyList(),
                    clientStatus = DiscordClientStatus()
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
    fun `Check onVoiceServerUpdate is called when event is received`() {
        countdownContext(1) {
            live.onVoiceServerUpdate {
                assertEquals(guildId, it.guildId)
                countDown()
            }

            fun createEvent(guildId: Snowflake) = VoiceServerUpdate(
                DiscordVoiceServerUpdateData(
                    guildId = guildId,
                    token = "",
                    endpoint = null
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
    fun `Check VoiceStateUpdateEvent is called when event is received`() {
        countdownContext(1) {
            live.onVoiceStateUpdate {
                assertEquals(guildId, it.state.guildId)
                countDown()
            }

            fun createEvent(guildId: Snowflake) = VoiceStateUpdate(
                DiscordVoiceState(
                    guildId = guildId.optionalSnowflake(),
                    channelId = null,
                    userId = randomId(),
                    sessionId = "",
                    deaf = false,
                    mute = false,
                    selfDeaf = false,
                    selfMute = false,
                    selfVideo = false,
                    suppress = false,
                    requestToSpeakTimestamp = null
                ),
                0
            )

            val eventRandomGuild = createEvent(randomId())
            sendEvent(eventRandomGuild)

            val event = createEvent(guildId)
            sendEvent(event)
        }
    }
}