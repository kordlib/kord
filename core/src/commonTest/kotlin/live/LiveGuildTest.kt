package dev.kord.core.live

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.optionalSnowflake
import dev.kord.core.cache.data.GuildData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.ReactionEmoji
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.live.exception.LiveCancellationException
import dev.kord.core.randomId
import dev.kord.gateway.*
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonObject
import kotlin.js.JsName
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.seconds

@Ignore
class LiveGuildTest : AbstractLiveEntityTest<LiveGuild>() {

    @BeforeTest
    fun onBefore() = runTest {
        live = LiveGuild(
            Guild(
                kord = kord,
                data = GuildData(
                    id = guildId,
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
    @JsName("test1")
    fun `Check onEmojisUpdate is called when event is received`() {
        countdownContext(1) {
            live.onEmojisUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test2")
    fun `Check onIntegrationsUpdate is called when event is received`() {
        countdownContext(1) {
            live.onIntegrationsUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test3")
    fun `Check onBanAdd is called when event is received`() {
        countdownContext(1) {
            live.onBanAdd {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test4")
    fun `Check onBanRemove is called when event is received`() {
        countdownContext(1) {
            live.onBanRemove {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test5")
    fun `Check onPresenceUpdate is called when event is received`() {
        countdownContext(1) {
            live.onPresenceUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test6")
    fun `Check onVoiceServerUpdate is called when event is received`() {
        countdownContext(1) {
            live.onVoiceServerUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test7")
    fun `Check onVoiceStateUpdate is called when event is received`() {
        countdownContext(1) {
            live.onVoiceStateUpdate {
                assertEquals(guildId, it.state.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test8")
    fun `Check onWebhookUpdate is called when event is received`() {
        countdownContext(1) {
            live.onWebhookUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test9")
    fun `Check onRoleCreate is called when event is received`() {
        countdownContext(1) {
            live.onRoleCreate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test10")
    fun `Check onRoleUpdate is called when event is received`() {
        countdownContext(1) {
            live.onRoleUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test11")
    fun `Check onRoleDelete is called when event is received`() {
        countdownContext(1) {
            live.onRoleDelete {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test12")
    fun `Check onMemberJoin is called when event is received`() {
        countdownContext(1) {
            live.onMemberJoin {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test13")
    fun `Check onMemberUpdate is called when event is received`() {
        countdownContext(1) {
            live.onMemberUpdate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test14")
    fun `Check onMemberLeave is called when event is received`() {
        countdownContext(1) {
            live.onMemberLeave {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test15")
    fun `Check onReactionAdd is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionAdd {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test16")
    fun `Check onReactionAdd with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionAdd(emojiExpected) {
                assertEquals(guildId, it.guildId)
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
            sendEventAndWait(createEvent(guildId, emojiOther))
            sendEvent(createEvent(guildId, emojiExpected))
        }
    }

    @Test
    @JsName("test17")
    fun `Check onReactionRemove is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")

            live.onReactionRemove {
                assertEquals(guildId, it.guildId)
                assertEquals(emojiExpected, it.emoji)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test18")
    fun `Check onReactionRemove with specific reaction is called when event is received`() {
        countdownContext(1) {
            val emojiExpected = ReactionEmoji.Unicode("\uD83D\uDC28")
            val emojiOther = ReactionEmoji.Unicode("\uD83D\uDC3B")

            live.onReactionRemove(emojiExpected) {
                assertEquals(guildId, it.guildId)
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
            sendEventAndWait(createEvent(guildId, emojiOther))
            sendEvent(createEvent(guildId, emojiExpected))
        }
    }

    @Test
    @JsName("test19")
    fun `Check onReactionRemoveAll is called when event is received`() {
        countdownContext(1) {
            live.onReactionRemoveAll {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test20")
    fun `Check onMessageCreate is called when event is received`() {
        countdownContext(1) {
            live.onMessageCreate {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test21")
    fun `Check onMessageUpdate is called when event is received`() {
        countdownContext(1) {
            live.onMessageUpdate {
                assertEquals(guildId, it.new.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test22")
    fun `Check onMessageDelete is called when event is received`() {
        countdownContext(1) {
            live.onMessageDelete {
                assertEquals(guildId, it.guildId)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test23")
    fun `Check onChannelCreate is called when event is received`() {
        countdownContext(1) {
            live.onChannelCreate {
                assertEquals(guildId, it.channel.data.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test24")
    fun `Check onChannelUpdate is called when event is received`() {
        countdownContext(1) {
            live.onChannelUpdate {
                assertEquals(guildId, it.channel.data.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test25")
    fun `Check onChannelDelete is called when event is received`() {
        countdownContext(1) {
            live.onChannelDelete {
                assertEquals(guildId, it.channel.data.guildId.value)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test26")
    fun `Check onGuildCreate is called when event is received`() {
        countdownContext(1) {
            live.onGuildCreate {
                assertEquals(guildId, it.guild.id)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test27")
    fun `Check onGuildUpdate is called when event is received`() {
        countdownContext(1) {
            live.onGuildUpdate {
                assertEquals(guildId, it.guild.id)
                count()
            }

            sendEventValidAndRandomId(guildId) {
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
    @JsName("test28")
    fun `Check if live entity is completed when event the guild delete event is received`()  {
        countdownContext(1) {
            live.coroutineContext.job.invokeOnCompletion {
                it as LiveCancellationException
                val event = it.event as GuildDeleteEvent
                assertEquals(guildId, event.guildId)
                runTest {
                    count()
                }
            }

            sendEventValidAndRandomIdCheckLiveActive(guildId) {
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
