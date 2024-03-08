package dev.kord.gateway.json

import dev.kord.common.entity.*
import dev.kord.gateway.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class DispatchEventSerializationTest {
    private fun String.nonBlankTrimmedLines() = lines().filter(String::isNotBlank).map(String::trim)

    private fun <T> testDispatchEventDeserialization(
        eventName: String,
        eventConstructor: (data: T, sequence: Int?) -> DispatchEvent,
        data: T,
        json: String,
    ) {
        val jsonVariationsWithMissingSequence = """
            {"op":0,"t":"$eventName","d":$json}
            {"op":0,"d":$json,"t":"$eventName"}
            {"t":"$eventName","op":0,"d":$json}
            {"t":"$eventName","d":$json,"op":0}
            {"d":$json,"op":0,"t":"$eventName"}
            {"d":$json,"t":"$eventName","op":0}
        """.nonBlankTrimmedLines()

        fun variations(s: String, d: String) = """
            {"op":0,"t":"$eventName","s":$s,"d":$d}
            {"op":0,"t":"$eventName","d":$d,"s":$s}
            {"op":0,"s":$s,"t":"$eventName","d":$d}
            {"op":0,"s":$s,"d":$d,"t":"$eventName"}
            {"op":0,"d":$d,"t":"$eventName","s":$s}
            {"op":0,"d":$d,"s":$s,"t":"$eventName"}

            {"t":"$eventName","op":0,"s":$s,"d":$d}
            {"t":"$eventName","op":0,"d":$d,"s":$s}
            {"t":"$eventName","s":$s,"op":0,"d":$d}
            {"t":"$eventName","s":$s,"d":$d,"op":0}
            {"t":"$eventName","d":$d,"op":0,"s":$s}
            {"t":"$eventName","d":$d,"s":$s,"op":0}

            {"s":$s,"op":0,"t":"$eventName","d":$d}
            {"s":$s,"op":0,"d":$d,"t":"$eventName"}
            {"s":$s,"t":"$eventName","op":0,"d":$d}
            {"s":$s,"t":"$eventName","d":$d,"op":0}
            {"s":$s,"d":$d,"op":0,"t":"$eventName"}
            {"s":$s,"d":$d,"t":"$eventName","op":0}

            {"d":$d,"op":0,"t":"$eventName","s":$s}
            {"d":$d,"op":0,"s":$s,"t":"$eventName"}
            {"d":$d,"t":"$eventName","op":0,"s":$s}
            {"d":$d,"t":"$eventName","s":$s,"op":0}
            {"d":$d,"s":$s,"op":0,"t":"$eventName"}
            {"d":$d,"s":$s,"t":"$eventName","op":0}
        """.nonBlankTrimmedLines()

        val sequence = Random.nextInt()
        val eventWithSequence = eventConstructor(data, sequence)
        val eventWithoutSequence = eventConstructor(data, null)

        for (jsonVariation in jsonVariationsWithMissingSequence) { // missing sequence
            assertEquals(eventWithoutSequence, Json.decodeFromString(Event.DeserializationStrategy, jsonVariation))
        }
        for (jsonVariation in variations(s = "null", d = json)) { // null sequence
            assertEquals(eventWithoutSequence, Json.decodeFromString(Event.DeserializationStrategy, jsonVariation))
        }
        for (jsonVariation in variations(s = sequence.toString(), d = json)) { // sequence present
            assertEquals(eventWithSequence, Json.decodeFromString(Event.DeserializationStrategy, jsonVariation))
        }
    }

    @Test
    fun test_GuildCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_CREATE",
        eventConstructor = ::GuildCreate,
        data = DiscordGuild(
            id = Snowflake.min,
            name = "name",
            icon = null,
            ownerId = Snowflake.min,
            region = "nice-region",
            afkChannelId = null,
            afkTimeout = 42.minutes,
            verificationLevel = VerificationLevel.Medium,
            defaultMessageNotifications = DefaultMessageNotificationLevel.OnlyMentions,
            explicitContentFilter = ExplicitContentFilter.MembersWithoutRoles,
            roles = emptyList(),
            emojis = emptyList(),
            features = emptyList(),
            mfaLevel = MFALevel.None,
            applicationId = null,
            systemChannelId = null,
            systemChannelFlags = SystemChannelFlags(),
            rulesChannelId = null,
            vanityUrlCode = null,
            description = null,
            banner = null,
            premiumTier = PremiumTier.One,
            preferredLocale = "en-US",
            publicUpdatesChannelId = null,
            nsfwLevel = NsfwLevel.Default,
            premiumProgressBarEnabled = false,
            safetyAlertsChannelId = null,
        ),
        json = """{"id":"0","name":"name","icon":null,"owner_id":"0","region":"nice-region","afk_channel_id":null,""" +
            """"afk_timeout":2520,"verification_level":2,"default_message_notifications":1,""" +
            """"explicit_content_filter":1,"roles":[],"emojis":[],"features":[],"mfa_level":0,""" +
            """"application_id":null,"system_channel_id":null,"system_channel_flags":0,"rules_channel_id":null,""" +
            """"vanity_url_code":null,"description":null,"banner":null,"premium_tier":1,"preferred_locale":"en-US",""" +
            """"public_updates_channel_id":null,"nsfw_level":0,"premium_progress_bar_enabled":false,""" +
            """"safety_alerts_channel_id":null}""",
    )

    @Test
    fun test_MessageReactionRemoveAll_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_REACTION_REMOVE_ALL",
        eventConstructor = ::MessageReactionRemoveAll,
        data = AllRemovedMessageReactions(channelId = Snowflake.min, messageId = Snowflake.min),
        json = """{"channel_id":"0","message_id":"0"}""",
    )

    @Test
    fun test_InviteDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "INVITE_DELETE",
        eventConstructor = ::InviteDelete,
        data = DiscordDeletedInvite(channelId = Snowflake.min, code = "code"),
        json = """{"channel_id":"0","code":"code"}""",
    )

    @Test
    fun test_MessageDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_DELETE",
        eventConstructor = ::MessageDelete,
        data = DeletedMessage(id = Snowflake.min, channelId = Snowflake.min),
        json = """{"id":"0","channel_id":"0"}""",
    )

    @Test
    fun test_VoiceStateUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "VOICE_STATE_UPDATE",
        eventConstructor = ::VoiceStateUpdate,
        data = DiscordVoiceState(
            channelId = null,
            userId = Snowflake.min,
            sessionId = "abcd",
            deaf = false,
            mute = false,
            selfDeaf = false,
            selfMute = false,
            selfVideo = false,
            suppress = false,
            requestToSpeakTimestamp = null,
        ),
        json = """{"channel_id":null,"user_id":"0","session_id":"abcd","deaf":false,"mute":false,"self_deaf":false,""" +
            """"self_mute":false,"self_video":false,"suppress":false,"request_to_speak_timestamp":null}"""
    )

    @Test
    fun test_GuildRoleDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_ROLE_DELETE",
        eventConstructor = ::GuildRoleDelete,
        data = DiscordDeletedGuildRole(guildId = Snowflake.min, id = Snowflake.min),
        json = """{"guild_id":"0","role_id":"0"}""",
    )

    private val instant = Clock.System.now()
    private val guildScheduledEvent = DiscordGuildScheduledEvent(
        id = Snowflake.min,
        guildId = Snowflake.min,
        channelId = null,
        name = "event",
        scheduledStartTime = instant,
        scheduledEndTime = null,
        privacyLevel = GuildScheduledEventPrivacyLevel.GuildOnly,
        status = GuildScheduledEventStatus.Active,
        entityType = ScheduledEntityType.External,
        entityId = null,
        entityMetadata = null,
    )
    private val guildScheduledEventJson = """{"id":"0","guild_id":"0","channel_id":null,"name":"event",""" +
        """"scheduled_start_time":"$instant","scheduled_end_time":null,"privacy_level":2,"status":2,""" +
        """"entity_type":3,"entity_id":null,"entity_metadata":null}"""

    @Test
    fun test_GuildScheduledEventDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_DELETE",
        eventConstructor = ::GuildScheduledEventDelete,
        data = guildScheduledEvent,
        json = guildScheduledEventJson,
    )

    @Test
    fun test_GuildScheduledEventCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_CREATE",
        eventConstructor = ::GuildScheduledEventCreate,
        data = guildScheduledEvent,
        json = guildScheduledEventJson,
    )

    @Test
    fun test_GuildRoleCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_ROLE_CREATE",
        eventConstructor = ::GuildRoleCreate,
        data = DiscordGuildRole(
            guildId = Snowflake.min,
            role = DiscordRole(
                id = Snowflake.min,
                name = "role",
                color = 0,
                hoist = false,
                position = 0,
                permissions = Permissions(),
                managed = false,
                mentionable = false,
                flags = RoleFlags(),
            ),
        ),
        json = """{"guild_id":"0","role":{"id":"0","name":"role","color":0,"hoist":false,"position":0,""" +
            """"permissions":"0","managed":false,"mentionable":false,"flags":0}}""",
    )

    @Test
    fun test_ApplicationCommandPermissionsUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "APPLICATION_COMMAND_PERMISSIONS_UPDATE",
        eventConstructor = ::ApplicationCommandPermissionsUpdate,
        data = DiscordGuildApplicationCommandPermissions(
            id = Snowflake.min,
            applicationId = Snowflake.min,
            guildId = Snowflake.min,
            permissions = emptyList(),
        ),
        json = """{"id":"0","application_id":"0","guild_id":"0","permissions":[]}""",
    )

    private val thread = DiscordChannel(id = Snowflake.min, type = ChannelType.PublicGuildThread)
    private val threadJson = """{"id":"0","type":11}"""

    @Test
    fun test_ThreadUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_UPDATE",
        eventConstructor = ::ThreadUpdate,
        data = thread,
        json = threadJson,
    )

    @Test
    fun test_ThreadCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_CREATE",
        eventConstructor = ::ThreadCreate,
        data = thread,
        json = threadJson,
    )

    @Test
    fun test_ThreadDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_DELETE",
        eventConstructor = ::ThreadDelete,
        data = thread,
        json = threadJson,
    )

    @Test
    fun test_ChannelCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "CHANNEL_CREATE",
        eventConstructor = ::ChannelCreate,
        data = DiscordChannel(id = Snowflake.min, type = ChannelType.GuildText),
        json = """{"id":"0","type":0}""",
    )

    @Test
    fun test_PresenceUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "PRESENCE_UPDATE",
        eventConstructor = ::PresenceUpdate,
        data = DiscordPresenceUpdate(
            user = DiscordPresenceUser(id = Snowflake.min, details = JsonObject(emptyMap())),
            status = PresenceStatus.Online,
            activities = emptyList(),
            clientStatus = DiscordClientStatus(),
        ),
        json = """{"user":{"id":"0"},"status":"online","activities":[],"client_status":{}}""",
    )

    @Test
    fun test_InteractionCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "INTERACTION_CREATE",
        eventConstructor = ::InteractionCreate,
        data = DiscordInteraction(
            id = Snowflake.min,
            applicationId = Snowflake.min,
            type = InteractionType.Ping,
            data = InteractionCallbackData(),
            token = "hunter2",
            version = 1,
        ),
        json = """{"id":"0","application_id":"0","type":1,"data":{},"token":"hunter2","version":1}""",
    )

    @Test
    fun test_TypingStart_deserialization() = testDispatchEventDeserialization(
        eventName = "TYPING_START",
        eventConstructor = ::TypingStart,
        data = DiscordTyping(
            channelId = Snowflake.min,
            userId = Snowflake.min,
            timestamp = Instant.fromEpochSeconds(123),
        ),
        json = """{"channel_id":"0","user_id":"0","timestamp":123}""",
    )

    @Test
    fun test_MessageReactionRemoveEmoji_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_REACTION_REMOVE_EMOJI",
        eventConstructor = ::MessageReactionRemoveEmoji,
        data = DiscordRemovedEmoji(
            channelId = Snowflake.min,
            guildId = Snowflake.min,
            messageId = Snowflake.min,
            emoji = DiscordRemovedReactionEmoji(id = null, name = null),
        ),
        json = """{"channel_id":"0","guild_id":"0","message_id":"0","emoji":{"id":null,"name":null}}""",
    )

    @Test
    fun test_GuildScheduledEventUserRemove_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_USER_REMOVE",
        eventConstructor = ::GuildScheduledEventUserRemove,
        data = GuildScheduledEventUserMetadata(
            guildScheduledEventId = Snowflake.min,
            userId = Snowflake.min,
            guildId = Snowflake.min,
        ),
        json = """{"guild_scheduled_event_id":"0","user_id":"0","guild_id":"0"}""",
    )

    @Test
    fun test_GuildBanRemove_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_BAN_REMOVE",
        eventConstructor = ::GuildBanRemove,
        data = DiscordGuildBan(
            guildId = Snowflake.min,
            user = DiscordUser(id = Snowflake.min, username = "username", avatar = null),
        ),
        json = """{"guild_id":"0","user":{"id":"0","username":"username","avatar":null}}""",
    )

    @Test
    fun test_ApplicationCommandDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "APPLICATION_COMMAND_DELETE",
        eventConstructor = ::ApplicationCommandDelete,
        data = DiscordApplicationCommand(
            id = Snowflake.min,
            applicationId = Snowflake.min,
            name = "name",
            description = null,
            defaultMemberPermissions = null,
            version = Snowflake.min,
        ),
        json = """{"id":"0","application_id":"0","name":"name","description":null,""" +
            """"default_member_permissions":null,"version":"0"}""",
    )

    @Test
    fun test_MessageUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_UPDATE",
        eventConstructor = ::MessageUpdate,
        data = DiscordPartialMessage(id = Snowflake.min, channelId = Snowflake.min),
        json = """{"id":"0","channel_id":"0"}""",
    )

    @Test
    fun test_AutoModerationRuleDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "AUTO_MODERATION_RULE_DELETE",
        eventConstructor = ::AutoModerationRuleDelete,
        data = DiscordAutoModerationRule(
            id = Snowflake.min,
            guildId = Snowflake.min,
            name = "rule",
            creatorId = Snowflake.min,
            eventType = AutoModerationRuleEventType.MessageSend,
            triggerType = AutoModerationRuleTriggerType.Spam,
            triggerMetadata = DiscordAutoModerationRuleTriggerMetadata(),
            actions = emptyList(),
            enabled = false,
            exemptRoles = emptyList(),
            exemptChannels = emptyList(),
        ),
        json = """{"id":"0","guild_id":"0","name":"rule","creator_id":"0","event_type":1,"trigger_type":3,""" +
            """"trigger_metadata":{},"actions":[],"enabled":false,"exempt_roles":[],"exempt_channels":[]}""",
    )

    @Test
    fun test_InviteCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "INVITE_CREATE",
        eventConstructor = ::InviteCreate,
        data = DiscordCreatedInvite(
            channelId = Snowflake.min,
            code = "code",
            createdAt = instant,
            maxAge = 100.hours,
            maxUses = 42,
            temporary = false,
            uses = 0,
        ),
        json = """{"channel_id":"0","code":"code","created_at":"$instant","max_age":360000,"max_uses":42,""" +
            """"temporary":false,"uses":0}""",
    )

    @Test
    fun test_Resumed_deserialization() = testDispatchEventDeserialization(
        eventName = "RESUMED",
        eventConstructor = { _, sequence -> Resumed(sequence) },
        data = null,
        json = "null",
    )

    @Test
    fun test_UnknownDispatchEvent_deserialization() {
        val missingDataEvent = UnknownDispatchEvent(name = null, data = JsonNull, sequence = null)
        val missingDataJsonVariations = """
            {"op":0}

            {"op":0,"t":null}
            {"t":null,"op":0}

            {"op":0,"s":null}
            {"s":null,"op":0}

            {"op":0,"t":null,"s":null}
            {"op":0,"s":null,"t":null}
            {"t":null,"op":0,"s":null}
            {"t":null,"s":null,"op":0}
            {"s":null,"op":0,"t":null}
            {"s":null,"t":null,"op":0}
        """.nonBlankTrimmedLines()
        for (json in missingDataJsonVariations) {
            assertEquals(missingDataEvent, Json.decodeFromString(Event.DeserializationStrategy, json))
        }
        val eventName = "SOME_UNKNOWN_EVENT"
        val jsonAndData = listOf(
            "null" to JsonNull,
            "1234" to JsonPrimitive(1234),
            "true" to JsonPrimitive(true),
            "\"str\"" to JsonPrimitive("str"),
            """[null,-1,false,""]""" to JsonArray(
                listOf(
                    JsonNull,
                    JsonPrimitive(-1),
                    JsonPrimitive(false),
                    JsonPrimitive(""),
                ),
            ),
            """{"a":null,"b":-134,"c":true,"d":"x"}""" to JsonObject(
                mapOf(
                    "a" to JsonNull,
                    "b" to JsonPrimitive(-134),
                    "c" to JsonPrimitive(true),
                    "d" to JsonPrimitive("x"),
                ),
            ),
        )
        for ((json, data) in jsonAndData) {
            testDispatchEventDeserialization(
                eventName,
                eventConstructor = { d, sequence -> UnknownDispatchEvent(eventName, d, sequence) },
                data = data,
                json = json,
            )
        }
    }
}
