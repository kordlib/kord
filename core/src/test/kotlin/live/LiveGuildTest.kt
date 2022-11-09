package live

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.*
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.gateway.*
import equality.randomId
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Timeout(value = 5, unit = TimeUnit.SECONDS)
@Disabled
class LiveGuildTest : AbstractLiveEntityTest<LiveGuild>() {

    @BeforeTest
    fun onBefore() = runBlocking {
        live = LiveGuild(
            Guild(
                kord = kord,
                data = GuildData(
                    id = guildId.value,
                    name = "",
                    ownerId = randomId(),
                    region = "",
                    afkTimeout = 0.seconds,
                    verificationLevel = VerificationLevel.None,
                    defaultMessageNotifications = DefaultMessageNotificationLevel.AllMessages,
                    explicitContentFilter = ExplicitContentFilter.Disabled,
                    roles = emptyList(),
                    emojis = emptyList(),
                    features = emptyList(),
                    mfaLevel = MFALevel.None,
                    premiumTier = PremiumTier.None,
                    preferredLocale = "",
                    systemChannelFlags = SystemChannelFlags(0),
                    nsfwLevel = NsfwLevel.Default,
                    premiumProgressBarEnabled = false
                )
            )
        )
    }

    @Test
    fun `Check onEmojisUpdate is called when event is received`() {
        countdownContext(1) {
            live.onEmojisUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildEmojisUpdate(
                    DiscordUpdatedEmojis(
                        guildId = it,
                        emojis = emptyList()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onIntegrationsUpdate is called when event is received`() {
        countdownContext(1) {
            live.onIntegrationsUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildIntegrationsUpdate(
                    DiscordGuildIntegrations(
                        guildId = it
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onBanAdd is called when event is received`() {
        countdownContext(1) {
            live.onBanAdd {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildBanAdd(
                    DiscordGuildBan(
                        guildId = it,
                        user = DiscordUser(
                            id = randomId(),
                            username = "",
                            discriminator = "",
                            avatar = null
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onBanRemove is called when event is received`() {
        countdownContext(1) {
            live.onBanRemove {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildBanRemove(
                    DiscordGuildBan(
                        guildId = it,
                        user = DiscordUser(
                            id = randomId(),
                            username = "",
                            discriminator = "",
                            avatar = null
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onPresenceUpdate is called when event is received`() {
        countdownContext(1) {
            live.onPresenceUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                PresenceUpdate(
                    DiscordPresenceUpdate(
                        user = DiscordPresenceUser(
                            id = randomId(),
                            details = JsonObject(emptyMap())
                        ),
                        guildId = it.optionalSnowflake(),
                        status = PresenceStatus.DoNotDisturb,
                        activities = emptyList(),
                        clientStatus = DiscordClientStatus()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onVoiceServerUpdate is called when event is received`() {
        countdownContext(1) {
            live.onVoiceServerUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                VoiceServerUpdate(
                    DiscordVoiceServerUpdateData(
                        guildId = it,
                        token = "",
                        endpoint = null
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onVoiceStateUpdate is called when event is received`() {
        countdownContext(1) {
            live.onVoiceStateUpdate {
                assertEquals(guildId.value, it.state.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                VoiceStateUpdate(
                    DiscordVoiceState(
                        guildId = it.optionalSnowflake(),
                        channelId = null,
                        userId = randomId(),
                        sessionId = "",
                        deaf = false,
                        mute = false,
                        selfDeaf = false,
                        selfMute = false,
                        selfVideo = false,
                        suppress = false,
                        requestToSpeakTimestamp = null,
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onWebhookUpdate is called when event is received`() {
        countdownContext(1) {
            live.onWebhookUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                WebhooksUpdate(
                    DiscordWebhooksUpdateData(
                        guildId = it,
                        channelId = randomId(),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onRoleCreate is called when event is received`() {
        countdownContext(1) {
            live.onRoleCreate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildRoleCreate(
                    DiscordGuildRole(
                        guildId = it,
                        role = DiscordRole(
                            id = randomId(),
                            name = "",
                            color = 0,
                            hoist = false,
                            icon = Optional.Missing(),
                            unicodeEmoji = Optional.Missing(),
                            position = 0,
                            permissions = Permissions(Permission.BanMembers),
                            managed = false,
                            mentionable = false
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onRoleUpdate is called when event is received`() {
        countdownContext(1) {
            live.onRoleUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildRoleUpdate(
                    DiscordGuildRole(
                        guildId = it,
                        role = DiscordRole(
                            id = randomId(),
                            name = "",
                            color = 0,
                            hoist = false,
                            icon = Optional.Missing(),
                            unicodeEmoji = Optional.Missing(),
                            position = 0,
                            permissions = Permissions(Permission.BanMembers),
                            managed = false,
                            mentionable = false
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onRoleDelete is called when event is received`() {
        countdownContext(1) {
            live.onRoleDelete {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildRoleDelete(
                    DiscordDeletedGuildRole(
                        guildId = it,
                        id = randomId()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onMemberJoin is called when event is received`() {
        countdownContext(1) {
            live.onMemberJoin {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildMemberAdd(
                    DiscordAddedGuildMember(
                        guildId = it,
                        user = Optional.invoke(
                            DiscordUser(
                                id = randomId(),
                                username = "",
                                discriminator = "",
                                avatar = null
                            )
                        ),
                        roles = emptyList(),
                        deaf = false,
                        mute = false,
                        joinedAt = Instant.fromEpochMilliseconds(0),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onMemberUpdate is called when event is received`() {
        countdownContext(1) {
            live.onMemberUpdate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildMemberUpdate(
                    DiscordUpdatedGuildMember(
                        guildId = it,
                        roles = emptyList(),
                        user = DiscordUser(
                            id = randomId(),
                            username = "",
                            discriminator = "",
                            avatar = null
                        ),
                        joinedAt = Instant.fromEpochMilliseconds(0),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onMemberLeave is called when event is received`() {
        countdownContext(1) {
            live.onMemberLeave {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildMemberRemove(
                    DiscordRemovedGuildMember(
                        guildId = it,
                        user = DiscordUser(
                            id = randomId(),
                            username = "",
                            discriminator = "",
                            avatar = null
                        )
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onReactionAdd is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionAdd {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                MessageReactionAdd(
                    MessageReactionAddData(
                        messageId = randomId(),
                        channelId = randomId(),
                        guildId = it.optionalSnowflake(),
                        userId = randomId(),
                        emoji = DiscordPartialEmoji(null, emojiExpected.name)
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onReactionAdd with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionAdd(emojiExpected) {
                assertEquals(guildId.value, it.guildId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            fun createEvent(guildId: Snowflake, emoji: ReactionEmoji) = MessageReactionAdd(
                MessageReactionAddData(
                    messageId = randomId(),
                    channelId = randomId(),
                    guildId = guildId.optionalSnowflake(),
                    userId = randomId(),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            sendEventAndWait(createEvent(randomId(), emojiExpected))
            sendEventAndWait(createEvent(guildId.value, emojiOther))
            sendEvent(createEvent(guildId.value, emojiExpected))
        }
    }

    @Test
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(guildId.value, it.guildId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                MessageReactionRemove(
                    MessageReactionRemoveData(
                        messageId = randomId(),
                        channelId = randomId(),
                        guildId = it.optionalSnowflake(),
                        userId = randomId(),
                        emoji = DiscordPartialEmoji(null, emojiExpected.name)
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onReactionRemove with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionRemove(emojiExpected) {
                assertEquals(guildId.value, it.guildId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            fun createEvent(guildId: Snowflake, emoji: ReactionEmoji) = MessageReactionRemove(
                MessageReactionRemoveData(
                    messageId = randomId(),
                    channelId = randomId(),
                    guildId = guildId.optionalSnowflake(),
                    userId = randomId(),
                    emoji = DiscordPartialEmoji(null, emoji.name)
                ),
                0
            )

            sendEventAndWait(createEvent(randomId(), emojiExpected))
            sendEventAndWait(createEvent(guildId.value, emojiOther))
            sendEvent(createEvent(guildId.value, emojiExpected))
        }
    }

    @Test
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            live.onReactionRemoveAll {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                MessageReactionRemoveAll(
                    AllRemovedMessageReactions(
                        channelId = randomId(),
                        messageId = randomId(),
                        guildId = it.optionalSnowflake()
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onMessageCreate is called when event is received`() {
        countdownContext(1) {
            live.onMessageCreate {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                MessageCreate(
                    DiscordMessage(
                        id = randomId(),
                        channelId = randomId(),
                        guildId = it.optionalSnowflake(),
                        author = DiscordUser(
                            id = randomId(),
                            username = "",
                            discriminator = "",
                            avatar = null
                        ),
                        content = "",
                        timestamp = Instant.fromEpochMilliseconds(0),
                        editedTimestamp = null,
                        tts = false,
                        mentionEveryone = false,
                        mentions = emptyList(),
                        mentionRoles = emptyList(),
                        attachments = emptyList(),
                        embeds = emptyList(),
                        pinned = false,
                        type = MessageType.Default
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onMessageUpdate is called when event is received`() {
        countdownContext(1) {
            live.onMessageUpdate {
                assertEquals(guildId.value, it.new.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                MessageUpdate(
                    DiscordPartialMessage(
                        id = randomId(),
                        channelId = randomId(),
                        guildId = it.optionalSnowflake(),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onMessageDelete is called when event is received`() {
        countdownContext(1) {
            live.onMessageDelete {
                assertEquals(guildId.value, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                MessageDelete(
                    DeletedMessage(
                        id = randomId(),
                        channelId = randomId(),
                        guildId = it.optionalSnowflake(),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onChannelCreate is called when event is received`() {
        countdownContext(1) {
            live.onChannelCreate {
                assertEquals(guildId.value, it.channel.data.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                ChannelCreate(
                    DiscordChannel(
                        id = randomId(),
                        type = ChannelType.GuildText,
                        guildId = it.optionalSnowflake(),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onChannelUpdate is called when event is received`() {
        countdownContext(1) {
            live.onChannelUpdate {
                assertEquals(guildId.value, it.channel.data.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                ChannelUpdate(
                    DiscordChannel(
                        id = randomId(),
                        type = ChannelType.GuildText,
                        guildId = it.optionalSnowflake(),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onChannelDelete is called when event is received`() {
        countdownContext(1) {
            live.onChannelDelete {
                assertEquals(guildId.value, it.channel.data.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                ChannelDelete(
                    DiscordChannel(
                        id = randomId(),
                        type = ChannelType.GuildText,
                        guildId = it.optionalSnowflake(),
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onGuildCreate is called when event is received`() {
        countdownContext(1) {
            live.onGuildCreate {
                assertEquals(guildId.value, it.guild.id)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildCreate(
                    DiscordGuild(
                        id = it,
                        name = "",
                        icon = null,
                        ownerId = randomId(),
                        region = "",
                        afkChannelId = null,
                        afkTimeout = 0.seconds,
                        verificationLevel = VerificationLevel.None,
                        defaultMessageNotifications = DefaultMessageNotificationLevel.AllMessages,
                        explicitContentFilter = ExplicitContentFilter.Disabled,
                        roles = emptyList(),
                        emojis = emptyList(),
                        features = emptyList(),
                        mfaLevel = MFALevel.None,
                        applicationId = null,
                        systemChannelId = null,
                        systemChannelFlags = SystemChannelFlags(0),
                        rulesChannelId = null,
                        vanityUrlCode = null,
                        description = null,
                        banner = null,
                        premiumTier = PremiumTier.None,
                        preferredLocale = "",
                        publicUpdatesChannelId = null,
                        nsfwLevel = NsfwLevel.Default,
                        premiumProgressBarEnabled = false
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check onGuildUpdate is called when event is received`() {
        countdownContext(1) {
            live.onGuildUpdate {
                assertEquals(guildId.value, it.guild.id)
                count()
            }

            sendEventValidAndRandomId(guildId.value) {
                GuildUpdate(
                    DiscordGuild(
                        id = it,
                        name = "",
                        icon = null,
                        ownerId = randomId(),
                        region = "",
                        afkChannelId = null,
                        afkTimeout = 0.seconds,
                        verificationLevel = VerificationLevel.None,
                        defaultMessageNotifications = DefaultMessageNotificationLevel.AllMessages,
                        explicitContentFilter = ExplicitContentFilter.Disabled,
                        roles = emptyList(),
                        emojis = emptyList(),
                        features = emptyList(),
                        mfaLevel = MFALevel.None,
                        applicationId = null,
                        systemChannelId = null,
                        systemChannelFlags = SystemChannelFlags(0),
                        rulesChannelId = null,
                        vanityUrlCode = null,
                        description = null,
                        banner = null,
                        premiumTier = PremiumTier.None,
                        preferredLocale = "",
                        publicUpdatesChannelId = null,
                        nsfwLevel = NsfwLevel.Default,
                        premiumProgressBarEnabled = false
                    ),
                    0
                )
            }
        }
    }

    @Test
    fun `Check if live entity is completed when event the guild delete event is received`() {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId.value, event.guildId)
                count()
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId.value) {
                GuildDelete(
                    DiscordUnavailableGuild(
                        id = it,
                    ),
                    0
                )
            }
        }
    }
}
