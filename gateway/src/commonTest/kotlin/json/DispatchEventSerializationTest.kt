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

    private fun testDispatchEventDeserialization(
        eventName: String,
        eventWithoutSequence: DispatchEvent,
        jsonWithoutSequence: String,
        eventWithSequence: DispatchEvent,
        jsonWithSequence: String,
    ) {
        val sequence = checkNotNull(eventWithSequence.sequence)
        check(eventWithoutSequence.sequence == null)
        check(eventWithSequence::class == eventWithoutSequence::class)

        val jsonVariationsWithMissingSequence = """
            {"op":0,"t":"$eventName","d":$jsonWithoutSequence}
            {"op":0,"d":$jsonWithoutSequence,"t":"$eventName"}
            {"t":"$eventName","op":0,"d":$jsonWithoutSequence}
            {"t":"$eventName","d":$jsonWithoutSequence,"op":0}
            {"d":$jsonWithoutSequence,"op":0,"t":"$eventName"}
            {"d":$jsonWithoutSequence,"t":"$eventName","op":0}
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

        for (json in jsonVariationsWithMissingSequence) { // missing sequence
            assertEquals(eventWithoutSequence, Json.decodeFromString(Event.DeserializationStrategy, json))
        }
        for (json in variations(s = "null", d = jsonWithoutSequence)) { // null sequence
            assertEquals(eventWithoutSequence, Json.decodeFromString(Event.DeserializationStrategy, json))
        }
        for (json in variations(s = sequence.toString(), d = jsonWithSequence)) { // sequence present
            assertEquals(eventWithSequence, Json.decodeFromString(Event.DeserializationStrategy, json))
        }
    }

    @Test
    fun test_GuildCreate_deserialization() {
        val guild = DiscordGuild(
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
        )
        val guildJson = """{"id":"0","name":"name","icon":null,"owner_id":"0","region":"nice-region",""" +
            """"afk_channel_id":null,"afk_timeout":2520,"verification_level":2,"default_message_notifications":1,""" +
            """"explicit_content_filter":1,"roles":[],"emojis":[],"features":[],"mfa_level":0,"application_id":null,""" +
            """"system_channel_id":null,"system_channel_flags":0,"rules_channel_id":null,"vanity_url_code":null,""" +
            """"description":null,"banner":null,"premium_tier":1,"preferred_locale":"en-US",""" +
            """"public_updates_channel_id":null,"nsfw_level":0,"premium_progress_bar_enabled":false,""" +
            """"safety_alerts_channel_id":null}"""
        testDispatchEventDeserialization(
            eventName = "GUILD_CREATE",
            eventWithoutSequence = GuildCreate(guild, sequence = null),
            jsonWithoutSequence = guildJson,
            eventWithSequence = GuildCreate(guild, sequence = Random.nextInt()),
            jsonWithSequence = guildJson,
        )
    }

    @Test
    fun test_MessageReactionRemoveAll_deserialization() {
        val removed = AllRemovedMessageReactions(channelId = Snowflake.min, messageId = Snowflake.min)
        val removedJson = """{"channel_id":"0","message_id":"0"}"""
        testDispatchEventDeserialization(
            eventName = "MESSAGE_REACTION_REMOVE_ALL",
            eventWithoutSequence = MessageReactionRemoveAll(removed, sequence = null),
            jsonWithoutSequence = removedJson,
            eventWithSequence = MessageReactionRemoveAll(removed, sequence = Random.nextInt()),
            jsonWithSequence = removedJson,
        )
    }

    @Test
    fun test_InviteDelete_deserialization() {
        val deleted = DiscordDeletedInvite(channelId = Snowflake.min, code = "code")
        val deletedJson = """{"channel_id":"0","code":"code"}"""
        testDispatchEventDeserialization(
            eventName = "INVITE_DELETE",
            eventWithoutSequence = InviteDelete(deleted, sequence = null),
            jsonWithoutSequence = deletedJson,
            eventWithSequence = InviteDelete(deleted, sequence = Random.nextInt()),
            jsonWithSequence = deletedJson,
        )
    }

    @Test
    fun test_MessageDelete_deserialization() {
        val deleted = DeletedMessage(id = Snowflake.min, channelId = Snowflake.min)
        val deletedJson = """{"id":"0","channel_id":"0"}"""
        testDispatchEventDeserialization(
            eventName = "MESSAGE_DELETE",
            eventWithoutSequence = MessageDelete(deleted, sequence = null),
            jsonWithoutSequence = deletedJson,
            eventWithSequence = MessageDelete(deleted, sequence = Random.nextInt()),
            jsonWithSequence = deletedJson,
        )
    }

    @Test
    fun test_VoiceStateUpdate_deserialization() {
        val voiceState = DiscordVoiceState(
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
        )
        val voiceStateJson = """{"channel_id":null,"user_id":"0","session_id":"abcd","deaf":false,"mute":false,""" +
            """"self_deaf":false,"self_mute":false,"self_video":false,"suppress":false,""" +
            """"request_to_speak_timestamp":null}"""
        testDispatchEventDeserialization(
            eventName = "VOICE_STATE_UPDATE",
            eventWithoutSequence = VoiceStateUpdate(voiceState, sequence = null),
            jsonWithoutSequence = voiceStateJson,
            eventWithSequence = VoiceStateUpdate(voiceState, sequence = Random.nextInt()),
            jsonWithSequence = voiceStateJson,
        )
    }

    @Test
    fun test_GuildRoleDelete_deserialization() {
        val deleted = DiscordDeletedGuildRole(guildId = Snowflake.min, id = Snowflake.min)
        val deletedJson = """{"guild_id":"0","role_id":"0"}"""
        testDispatchEventDeserialization(
            eventName = "GUILD_ROLE_DELETE",
            eventWithoutSequence = GuildRoleDelete(deleted, sequence = null),
            jsonWithoutSequence = deletedJson,
            eventWithSequence = GuildRoleDelete(deleted, sequence = Random.nextInt()),
            jsonWithSequence = deletedJson,
        )
    }

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
        eventWithoutSequence = GuildScheduledEventDelete(guildScheduledEvent, sequence = null),
        jsonWithoutSequence = guildScheduledEventJson,
        eventWithSequence = GuildScheduledEventDelete(guildScheduledEvent, sequence = Random.nextInt()),
        jsonWithSequence = guildScheduledEventJson,
    )

    @Test
    fun test_GuildScheduledEventCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_CREATE",
        eventWithoutSequence = GuildScheduledEventCreate(guildScheduledEvent, sequence = null),
        jsonWithoutSequence = guildScheduledEventJson,
        eventWithSequence = GuildScheduledEventCreate(guildScheduledEvent, sequence = Random.nextInt()),
        jsonWithSequence = guildScheduledEventJson,
    )

    @Test
    fun test_GuildRoleCreate_deserialization() {
        val role = DiscordGuildRole(
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
        )
        val roleJson = """{"guild_id":"0","role":{"id":"0","name":"role","color":0,"hoist":false,"position":0,""" +
            """"permissions":"0","managed":false,"mentionable":false,"flags":0}}"""
        testDispatchEventDeserialization(
            eventName = "GUILD_ROLE_CREATE",
            eventWithoutSequence = GuildRoleCreate(role, sequence = null),
            jsonWithoutSequence = roleJson,
            eventWithSequence = GuildRoleCreate(role, sequence = Random.nextInt()),
            jsonWithSequence = roleJson,
        )
    }

    @Test
    fun test_ApplicationCommandPermissionsUpdate_deserialization() {
        val permissions = DiscordGuildApplicationCommandPermissions(
            id = Snowflake.min,
            applicationId = Snowflake.min,
            guildId = Snowflake.min,
            permissions = emptyList(),
        )
        val permissionsJson = """{"id":"0","application_id":"0","guild_id":"0","permissions":[]}"""
        testDispatchEventDeserialization(
            eventName = "APPLICATION_COMMAND_PERMISSIONS_UPDATE",
            eventWithoutSequence = ApplicationCommandPermissionsUpdate(permissions, sequence = null),
            jsonWithoutSequence = permissionsJson,
            eventWithSequence = ApplicationCommandPermissionsUpdate(permissions, sequence = Random.nextInt()),
            jsonWithSequence = permissionsJson,
        )
    }

    private val thread = DiscordChannel(id = Snowflake.min, type = ChannelType.PublicGuildThread)
    private val threadJson = """{"id":"0","type":11}"""

    @Test
    fun test_ThreadUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_UPDATE",
        eventWithoutSequence = ThreadUpdate(thread, sequence = null),
        jsonWithoutSequence = threadJson,
        eventWithSequence = ThreadUpdate(thread, sequence = Random.nextInt()),
        jsonWithSequence = threadJson,
    )

    @Test
    fun test_ThreadCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_CREATE",
        eventWithoutSequence = ThreadCreate(thread, sequence = null),
        jsonWithoutSequence = threadJson,
        eventWithSequence = ThreadCreate(thread, sequence = Random.nextInt()),
        jsonWithSequence = threadJson,
    )

    @Test
    fun test_ThreadDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_DELETE",
        eventWithoutSequence = ThreadDelete(thread, sequence = null),
        jsonWithoutSequence = threadJson,
        eventWithSequence = ThreadDelete(thread, sequence = Random.nextInt()),
        jsonWithSequence = threadJson,
    )

    @Test
    fun test_ChannelCreate_deserialization() {
        val channel = DiscordChannel(id = Snowflake.min, type = ChannelType.GuildText)
        val channelJson = """{"id":"0","type":0}"""
        testDispatchEventDeserialization(
            eventName = "CHANNEL_CREATE",
            eventWithoutSequence = ChannelCreate(channel, sequence = null),
            jsonWithoutSequence = channelJson,
            eventWithSequence = ChannelCreate(channel, sequence = Random.nextInt()),
            jsonWithSequence = channelJson,
        )
    }

    @Test
    fun test_PresenceUpdate_deserialization() {
        val presence = DiscordPresenceUpdate(
            user = DiscordPresenceUser(id = Snowflake.min, details = JsonObject(emptyMap())),
            status = PresenceStatus.Online,
            activities = emptyList(),
            clientStatus = DiscordClientStatus(),
        )
        val presenceJson = """{"user":{"id":"0"},"status":"online","activities":[],"client_status":{}}"""
        testDispatchEventDeserialization(
            eventName = "PRESENCE_UPDATE",
            eventWithoutSequence = PresenceUpdate(presence, sequence = null),
            jsonWithoutSequence = presenceJson,
            eventWithSequence = PresenceUpdate(presence, sequence = Random.nextInt()),
            jsonWithSequence = presenceJson,
        )
    }

    @Test
    fun test_InteractionCreate_deserialization() {
        val interaction = DiscordInteraction(
            id = Snowflake.min,
            applicationId = Snowflake.min,
            type = InteractionType.Ping,
            data = InteractionCallbackData(),
            token = "hunter2",
            version = 1,
        )
        val interactionJson = """{"id":"0","application_id":"0","type":1,"data":{},"token":"hunter2","version":1}"""
        testDispatchEventDeserialization(
            eventName = "INTERACTION_CREATE",
            eventWithoutSequence = InteractionCreate(interaction, sequence = null),
            jsonWithoutSequence = interactionJson,
            eventWithSequence = InteractionCreate(interaction, sequence = Random.nextInt()),
            jsonWithSequence = interactionJson,
        )
    }

    @Test
    fun test_TypingStart_deserialization() {
        val typing = DiscordTyping(
            channelId = Snowflake.min,
            userId = Snowflake.min,
            timestamp = Instant.fromEpochSeconds(123),
        )
        val typingJson = """{"channel_id":"0","user_id":"0","timestamp":123}"""
        testDispatchEventDeserialization(
            eventName = "TYPING_START",
            eventWithoutSequence = TypingStart(typing, sequence = null),
            jsonWithoutSequence = typingJson,
            eventWithSequence = TypingStart(typing, sequence = Random.nextInt()),
            jsonWithSequence = typingJson,
        )
    }

    @Test
    fun test_MessageReactionRemoveEmoji_deserialization() {
        val removed = DiscordRemovedEmoji(
            channelId = Snowflake.min,
            guildId = Snowflake.min,
            messageId = Snowflake.min,
            emoji = DiscordRemovedReactionEmoji(id = null, name = null),
        )
        val removedJson = """{"channel_id":"0","guild_id":"0","message_id":"0","emoji":{"id":null,"name":null}}"""
        testDispatchEventDeserialization(
            eventName = "MESSAGE_REACTION_REMOVE_EMOJI",
            eventWithoutSequence = MessageReactionRemoveEmoji(removed, sequence = null),
            jsonWithoutSequence = removedJson,
            eventWithSequence = MessageReactionRemoveEmoji(removed, sequence = Random.nextInt()),
            jsonWithSequence = removedJson,
        )
    }

    @Test
    fun test_GuildScheduledEventUserRemove_deserialization() {
        val data = GuildScheduledEventUserMetadata(
            guildScheduledEventId = Snowflake.min,
            userId = Snowflake.min,
            guildId = Snowflake.min,
        )
        val dataJson = """{"guild_scheduled_event_id":"0","user_id":"0","guild_id":"0"}"""
        testDispatchEventDeserialization(
            eventName = "GUILD_SCHEDULED_EVENT_USER_REMOVE",
            eventWithoutSequence = GuildScheduledEventUserRemove(data, sequence = null),
            jsonWithoutSequence = dataJson,
            eventWithSequence = GuildScheduledEventUserRemove(data, sequence = Random.nextInt()),
            jsonWithSequence = dataJson,
        )
    }

    @Test
    fun test_GuildBanRemove_deserialization() {
        val ban = DiscordGuildBan(
            guildId = Snowflake.min,
            user = DiscordUser(id = Snowflake.min, username = "username", avatar = null),
        )
        val banJson = """{"guild_id":"0","user":{"id":"0","username":"username","avatar":null}}"""
        testDispatchEventDeserialization(
            eventName = "GUILD_BAN_REMOVE",
            eventWithoutSequence = GuildBanRemove(ban, sequence = null),
            jsonWithoutSequence = banJson,
            eventWithSequence = GuildBanRemove(ban, sequence = Random.nextInt()),
            jsonWithSequence = banJson,
        )
    }

    @Test
    fun test_ApplicationCommandDelete_deserialization() {
        val command = DiscordApplicationCommand(
            id = Snowflake.min,
            applicationId = Snowflake.min,
            name = "name",
            description = null,
            defaultMemberPermissions = null,
            version = Snowflake.min,
        )
        val commandJson = """{"id":"0","application_id":"0","name":"name","description":null,""" +
            """"default_member_permissions":null,"version":"0"}"""
        testDispatchEventDeserialization(
            eventName = "APPLICATION_COMMAND_DELETE",
            eventWithoutSequence = ApplicationCommandDelete(command, sequence = null),
            jsonWithoutSequence = commandJson,
            eventWithSequence = ApplicationCommandDelete(command, sequence = Random.nextInt()),
            jsonWithSequence = commandJson,
        )
    }

    @Test
    fun test_MessageUpdate_deserialization() {
        val message = DiscordPartialMessage(id = Snowflake.min, channelId = Snowflake.min)
        val messageJson = """{"id":"0","channel_id":"0"}"""
        testDispatchEventDeserialization(
            eventName = "MESSAGE_UPDATE",
            eventWithoutSequence = MessageUpdate(message, sequence = null),
            jsonWithoutSequence = messageJson,
            eventWithSequence = MessageUpdate(message, sequence = Random.nextInt()),
            jsonWithSequence = messageJson,
        )
    }

    @Test
    fun test_AutoModerationRuleDelete_deserialization() {
        val rule = DiscordAutoModerationRule(
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
        )
        val ruleJson = """{"id":"0","guild_id":"0","name":"rule","creator_id":"0","event_type":1,"trigger_type":3,""" +
            """"trigger_metadata":{},"actions":[],"enabled":false,"exempt_roles":[],"exempt_channels":[]}"""
        testDispatchEventDeserialization(
            eventName = "AUTO_MODERATION_RULE_DELETE",
            eventWithoutSequence = AutoModerationRuleDelete(rule, sequence = null),
            jsonWithoutSequence = ruleJson,
            eventWithSequence = AutoModerationRuleDelete(rule, sequence = Random.nextInt()),
            jsonWithSequence = ruleJson,
        )
    }

    @Test
    fun test_InviteCreate_deserialization() {
        val invite = DiscordCreatedInvite(
            channelId = Snowflake.min,
            code = "code",
            createdAt = instant,
            maxAge = 100.hours,
            maxUses = 42,
            temporary = false,
            uses = 0,
        )
        val inviteJson = """{"channel_id":"0","code":"code","created_at":"$instant","max_age":360000,""" +
            """"max_uses":42,"temporary":false,"uses":0}"""
        testDispatchEventDeserialization(
            eventName = "INVITE_CREATE",
            eventWithoutSequence = InviteCreate(invite, sequence = null),
            jsonWithoutSequence = inviteJson,
            eventWithSequence = InviteCreate(invite, sequence = Random.nextInt()),
            jsonWithSequence = inviteJson,
        )
    }

    @Test
    fun test_Resumed_deserialization() = testDispatchEventDeserialization(
        eventName = "RESUMED",
        eventWithoutSequence = Resumed(sequence = null),
        jsonWithoutSequence = "null",
        eventWithSequence = Resumed(sequence = Random.nextInt()),
        jsonWithSequence = "null",
    )

    @Test
    fun test_UnknownDispatchEvent_deserialization() {
        val missingDataEvent = UnknownDispatchEvent(name = null, data = null, sequence = null)
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
                eventWithoutSequence = UnknownDispatchEvent(eventName, data, sequence = null),
                jsonWithoutSequence = json,
                eventWithSequence = UnknownDispatchEvent(eventName, data, sequence = Random.nextInt()),
                jsonWithSequence = json,
            )
        }
    }
}
