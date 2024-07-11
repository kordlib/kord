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

class DispatchEventDeserializationTest {
    private fun <T> testDispatchEventDeserialization(
        eventName: String,
        eventConstructor: (data: T, sequence: Int?) -> DispatchEvent,
        data: T,
        json: String,
    ) {
        val sequence = Random.nextInt()
        val eventWithoutSequence = eventConstructor(data, null)
        val eventWithSequence = eventConstructor(data, sequence)

        val permutationsWithMissingSequence =
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "d" to json)
        val permutationsWithNullSequence =
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "null", "d" to json)
        val permutationsWithSequence =
            jsonObjectPermutations("op" to "0", "t" to "\"$eventName\"", "s" to "$sequence", "d" to json)

        permutationsWithMissingSequence.forEach { perm ->
            assertEquals(eventWithoutSequence, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
        permutationsWithNullSequence.forEach { perm ->
            assertEquals(eventWithoutSequence, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
        permutationsWithSequence.forEach { perm ->
            assertEquals(eventWithSequence, Json.decodeFromString(Event.DeserializationStrategy, perm))
        }
    }


    private val autoModerationRule = DiscordAutoModerationRule(
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
    private val autoModerationRuleJson = """{"id":"0","guild_id":"0","name":"rule","creator_id":"0",""" +
        """"event_type":1,"trigger_type":3,"trigger_metadata":{},"actions":[],"enabled":false,"exempt_roles":[],""" +
        """"exempt_channels":[]}"""
    private val channel = DiscordChannel(id = Snowflake.min, type = ChannelType.GuildText)
    private val channelJson = """{"id":"0","type":0}"""
    private val thread = DiscordChannel(id = Snowflake.min, type = ChannelType.PublicGuildThread)
    private val threadJson = """{"id":"0","type":11}"""
    private val guild = DiscordGuild(
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
    private val guildJson = """{"id":"0","name":"name","icon":null,"owner_id":"0","region":"nice-region",""" +
        """"afk_channel_id":null,"afk_timeout":2520,"verification_level":2,"default_message_notifications":1,""" +
        """"explicit_content_filter":1,"roles":[],"emojis":[],"features":[],"mfa_level":0,"application_id":null,""" +
        """"system_channel_id":null,"system_channel_flags":0,"rules_channel_id":null,"vanity_url_code":null,""" +
        """"description":null,"banner":null,"premium_tier":1,"preferred_locale":"en-US",""" +
        """"public_updates_channel_id":null,"nsfw_level":0,"premium_progress_bar_enabled":false,""" +
        """"safety_alerts_channel_id":null}"""
    private val user = DiscordUser(id = Snowflake.min, username = "username", avatar = null)
    private val userJson = """{"id":"0","username":"username","avatar":null}"""
    private val guildBan = DiscordGuildBan(guildId = Snowflake.min, user = user)
    private val guildBanJson = """{"guild_id":"0","user":$userJson}"""
    private val guildRole = DiscordGuildRole(
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
    private val guildRoleJson = """{"guild_id":"0","role":{"id":"0","name":"role","color":0,"hoist":false,""" +
        """"position":0,"permissions":"0","managed":false,"mentionable":false,"flags":0}}"""
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
    private val guildScheduledEventUserMetadata = GuildScheduledEventUserMetadata(
        guildScheduledEventId = Snowflake.min,
        userId = Snowflake.min,
        guildId = Snowflake.min,
    )
    private val guildScheduledEventUserMetadataJson =
        """{"guild_scheduled_event_id":"0","user_id":"0","guild_id":"0"}"""
    private val integration = DiscordIntegration(
        id = Snowflake.min,
        name = "name",
        type = "discord",
        enabled = true,
        account = DiscordIntegrationsAccount(id = "id", name = "name"),
    )
    private val integrationJson =
        """{"id":"0","name":"name","type":"discord","enabled":true,"account":{"id":"id","name":"name"}}"""


    /*
     * Keep tests ordered like this table: https://discord.com/developers/docs/topics/gateway-events#receive-events
     * (Hello, Reconnect and InvalidSession are tested elsewhere, they are no DispatchEvents)
     */


    @Test
    fun test_Ready_deserialization() = testDispatchEventDeserialization(
        eventName = "READY",
        eventConstructor = ::Ready,
        data = ReadyData(
            version = 42,
            user = user,
            privateChannels = emptyList(),
            guilds = emptyList(),
            sessionId = "deadbeef",
            resumeGatewayUrl = "wss://example.com",
            traces = emptyList(),
        ),
        json = """{"v":42,"user":$userJson,"private_channels":[],"guilds":[],"session_id":"deadbeef",""" +
            """"resume_gateway_url":"wss://example.com","_trace":[]}""",
    )

    @Test
    fun test_Resumed_deserialization() = testDispatchEventDeserialization(
        eventName = "RESUMED",
        eventConstructor = { _, sequence -> Resumed(sequence) },
        data = null,
        json = "null",
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

    @Test
    fun test_AutoModerationRuleCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "AUTO_MODERATION_RULE_CREATE",
        eventConstructor = ::AutoModerationRuleCreate,
        data = autoModerationRule,
        json = autoModerationRuleJson,
    )

    @Test
    fun test_AutoModerationRuleUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "AUTO_MODERATION_RULE_UPDATE",
        eventConstructor = ::AutoModerationRuleUpdate,
        data = autoModerationRule,
        json = autoModerationRuleJson,
    )

    @Test
    fun test_AutoModerationRuleDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "AUTO_MODERATION_RULE_DELETE",
        eventConstructor = ::AutoModerationRuleDelete,
        data = autoModerationRule,
        json = autoModerationRuleJson,
    )

    @Test
    fun test_AutoModerationActionExecution_deserialization() = testDispatchEventDeserialization(
        eventName = "AUTO_MODERATION_ACTION_EXECUTION",
        eventConstructor = ::AutoModerationActionExecution,
        data = DiscordAutoModerationActionExecution(
            guildId = Snowflake.min,
            action = DiscordAutoModerationAction(type = AutoModerationActionType.BlockMessage),
            ruleId = Snowflake.min,
            ruleTriggerType = AutoModerationRuleTriggerType.Keyword,
            userId = Snowflake.min,
            content = "evil",
            matchedKeyword = "ev",
            matchedContent = "ev",
        ),
        json = """{"guild_id":"0","action":{"type":1},"rule_id":"0","rule_trigger_type":1,"user_id":"0",""" +
            """"content":"evil","matched_keyword":"ev","matched_content":"ev"}""",
    )

    @Test
    fun test_ChannelCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "CHANNEL_CREATE",
        eventConstructor = ::ChannelCreate,
        data = channel,
        json = channelJson,
    )

    @Test
    fun test_ChannelUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "CHANNEL_UPDATE",
        eventConstructor = ::ChannelUpdate,
        data = channel,
        json = channelJson,
    )

    @Test
    fun test_ChannelDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "CHANNEL_DELETE",
        eventConstructor = ::ChannelDelete,
        data = channel,
        json = channelJson,
    )

    @Test
    fun test_ChannelPinsUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "CHANNEL_PINS_UPDATE",
        eventConstructor = ::ChannelPinsUpdate,
        data = DiscordPinsUpdateData(channelId = Snowflake.min),
        json = """{"channel_id":"0"}""",
    )

    @Test
    fun test_ThreadCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_CREATE",
        eventConstructor = ::ThreadCreate,
        data = thread,
        json = threadJson,
    )

    @Test
    fun test_ThreadUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_UPDATE",
        eventConstructor = ::ThreadUpdate,
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
    fun test_ThreadListSync_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_LIST_SYNC",
        eventConstructor = ::ThreadListSync,
        data = DiscordThreadListSync(guildId = Snowflake.min, threads = emptyList(), members = emptyList()),
        json = """{"guild_id":"0","threads":[],"members":[]}""",
    )

    @Test
    fun test_ThreadMemberUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_MEMBER_UPDATE",
        eventConstructor = ::ThreadMemberUpdate,
        data = DiscordThreadMember(joinTimestamp = instant, flags = 0),
        json = """{"join_timestamp":"$instant","flags":0}""",
    )

    @Test
    fun test_ThreadMembersUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "THREAD_MEMBERS_UPDATE",
        eventConstructor = ::ThreadMembersUpdate,
        data = DiscordThreadMembersUpdate(id = Snowflake.min, guildId = Snowflake.min, memberCount = 42),
        json = """{"id":"0","guild_id":"0","member_count":42}""",
    )

    private val entitlement = DiscordEntitlement(
        id = Snowflake.min,
        applicationId = Snowflake.min,
        deleted = false,
        skuId = Snowflake.min,
        type = EntitlementType.ApplicationSubscription,
    )
    private val entitlementJson = """{"id":"0","application_id":"0","sku_id":"0","type":8,"deleted":false}"""

    @Test
    fun test_EntitlementCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "ENTITLEMENT_CREATE",
        eventConstructor = ::EntitlementCreate,
        data = entitlement,
        json = entitlementJson,
    )

    @Test
    fun test_EntitlementUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "ENTITLEMENT_UPDATE",
        eventConstructor = ::EntitlementUpdate,
        data = entitlement,
        json = entitlementJson,
    )

    @Test
    fun test_EntitlementDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "ENTITLEMENT_DELETE",
        eventConstructor = ::EntitlementDelete,
        data = entitlement,
        json = entitlementJson,
    )

    @Test
    fun test_GuildCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_CREATE",
        eventConstructor = ::GuildCreate,
        data = guild,
        json = guildJson,
    )

    @Test
    fun test_GuildUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_UPDATE",
        eventConstructor = ::GuildUpdate,
        data = guild,
        json = guildJson,
    )

    @Test
    fun test_GuildDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_DELETE",
        eventConstructor = ::GuildDelete,
        data = DiscordUnavailableGuild(id = Snowflake.min),
        json = """{"id":"0"}""",
    )

    @Test
    fun test_GuildAuditLogEntryCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_AUDIT_LOG_ENTRY_CREATE",
        eventConstructor = ::GuildAuditLogEntryCreate,
        data = DiscordAuditLogEntry(
            targetId = null,
            userId = null,
            id = Snowflake.min,
            actionType = AuditLogEvent.MemberKick,
        ),
        json = """{"target_id":null,"user_id":null,"id":"0","action_type":20}""",
    )

    @Test
    fun test_GuildBanAdd_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_BAN_ADD",
        eventConstructor = ::GuildBanAdd,
        data = guildBan,
        json = guildBanJson,
    )

    @Test
    fun test_GuildBanRemove_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_BAN_REMOVE",
        eventConstructor = ::GuildBanRemove,
        data = guildBan,
        json = guildBanJson,
    )

    @Test
    fun test_GuildEmojisUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_EMOJIS_UPDATE",
        eventConstructor = ::GuildEmojisUpdate,
        data = DiscordUpdatedEmojis(guildId = Snowflake.min, emojis = emptyList()),
        json = """{"guild_id":"0","emojis":[]}""",
    )

    /*
     * Missing:
     * - GuildStickersUpdate
     */

    @Test
    fun test_GuildIntegrationsUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_INTEGRATIONS_UPDATE",
        eventConstructor = ::GuildIntegrationsUpdate,
        data = DiscordGuildIntegrations(guildId = Snowflake.min),
        json = """{"guild_id":"0"}""",
    )

    @Test
    fun test_GuildMemberAdd_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_MEMBER_ADD",
        eventConstructor = ::GuildMemberAdd,
        data = DiscordAddedGuildMember(
            roles = emptyList(),
            joinedAt = instant,
            deaf = false,
            mute = false,
            flags = GuildMemberFlags(),
            guildId = Snowflake.min,
        ),
        json = """{"roles":[],"joined_at":"$instant","deaf":false,"mute":false,"flags":0,"guild_id":"0"}""",
    )

    @Test
    fun test_GuildMemberRemove_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_MEMBER_REMOVE",
        eventConstructor = ::GuildMemberRemove,
        data = DiscordRemovedGuildMember(guildId = Snowflake.min, user = user),
        json = """{"guild_id":"0","user":$userJson}""",
    )

    @Test
    fun test_GuildMemberUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_MEMBER_UPDATE",
        eventConstructor = ::GuildMemberUpdate,
        data = DiscordUpdatedGuildMember(
            guildId = Snowflake.min,
            roles = emptyList(),
            user = user,
            joinedAt = instant,
            flags = GuildMemberFlags(),
        ),
        json = """{"guild_id":"0","roles":[],"user":$userJson,"joined_at":"$instant","flags":0}""",
    )

    @Test
    fun test_GuildMembersChunk_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_MEMBERS_CHUNK",
        eventConstructor = ::GuildMembersChunk,
        data = GuildMembersChunkData(
            guildId = Snowflake.min,
            members = emptyList(),
            chunkIndex = 42,
            chunkCount = 9001,
        ),
        json = """{"guild_id":"0","members":[],"chunk_index":42,"chunk_count":9001}""",
    )

    @Test
    fun test_GuildRoleCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_ROLE_CREATE",
        eventConstructor = ::GuildRoleCreate,
        data = guildRole,
        json = guildRoleJson,
    )

    @Test
    fun test_GuildRoleUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_ROLE_UPDATE",
        eventConstructor = ::GuildRoleUpdate,
        data = guildRole,
        json = guildRoleJson,
    )

    @Test
    fun test_GuildRoleDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_ROLE_DELETE",
        eventConstructor = ::GuildRoleDelete,
        data = DiscordDeletedGuildRole(guildId = Snowflake.min, id = Snowflake.min),
        json = """{"guild_id":"0","role_id":"0"}""",
    )

    @Test
    fun test_GuildScheduledEventCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_CREATE",
        eventConstructor = ::GuildScheduledEventCreate,
        data = guildScheduledEvent,
        json = guildScheduledEventJson,
    )

    @Test
    fun test_GuildScheduledEventUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_UPDATE",
        eventConstructor = ::GuildScheduledEventUpdate,
        data = guildScheduledEvent,
        json = guildScheduledEventJson,
    )

    @Test
    fun test_GuildScheduledEventDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_DELETE",
        eventConstructor = ::GuildScheduledEventDelete,
        data = guildScheduledEvent,
        json = guildScheduledEventJson,
    )

    @Test
    fun test_GuildScheduledEventUserAdd_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_USER_ADD",
        eventConstructor = ::GuildScheduledEventUserAdd,
        data = guildScheduledEventUserMetadata,
        json = guildScheduledEventUserMetadataJson,
    )

    @Test
    fun test_GuildScheduledEventUserRemove_deserialization() = testDispatchEventDeserialization(
        eventName = "GUILD_SCHEDULED_EVENT_USER_REMOVE",
        eventConstructor = ::GuildScheduledEventUserRemove,
        data = guildScheduledEventUserMetadata,
        json = guildScheduledEventUserMetadataJson,
    )

    @Test
    fun test_IntegrationCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "INTEGRATION_CREATE",
        eventConstructor = ::IntegrationCreate,
        data = integration,
        json = integrationJson,
    )

    @Test
    fun test_IntegrationUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "INTEGRATION_UPDATE",
        eventConstructor = ::IntegrationUpdate,
        data =
        integration,
        json = integrationJson,
    )

    @Test
    fun test_IntegrationDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "INTEGRATION_DELETE",
        eventConstructor = ::IntegrationDelete,
        data = DiscordIntegrationDelete(id = Snowflake.min, guildId = Snowflake.min),
        json = """{"id":"0","guild_id":"0"}""",
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
    fun test_InviteDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "INVITE_DELETE",
        eventConstructor = ::InviteDelete,
        data = DiscordDeletedInvite(channelId = Snowflake.min, code = "code"),
        json = """{"channel_id":"0","code":"code"}""",
    )

    @Test
    fun test_MessageCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_CREATE",
        eventConstructor = ::MessageCreate,
        data = DiscordMessage(
            id = Snowflake.min,
            channelId = Snowflake.min,
            author = user,
            content = "hi",
            timestamp = instant,
            editedTimestamp = null,
            tts = false,
            mentionEveryone = false,
            mentions = emptyList(),
            mentionRoles = emptyList(),
            attachments = emptyList(),
            embeds = emptyList(),
            pinned = false,
            type = MessageType.Default,
        ),
        json = """{"id":"0","channel_id":"0","author":$userJson,"content":"hi","timestamp":"$instant",""" +
            """"edited_timestamp":null,"tts":false,"mention_everyone":false,"mentions":[],"mention_roles":[],""" +
            """"attachments":[],"embeds":[],"pinned":false,"type":0}""",
    )

    @Test
    fun test_MessageUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_UPDATE",
        eventConstructor = ::MessageUpdate,
        data = DiscordPartialMessage(id = Snowflake.min, channelId = Snowflake.min),
        json = """{"id":"0","channel_id":"0"}""",
    )

    @Test
    fun test_MessageDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_DELETE",
        eventConstructor = ::MessageDelete,
        data = DeletedMessage(id = Snowflake.min, channelId = Snowflake.min),
        json = """{"id":"0","channel_id":"0"}""",
    )

    @Test
    fun test_MessageDeleteBulk_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_DELETE_BULK",
        eventConstructor = ::MessageDeleteBulk,
        data = BulkDeleteData(ids = emptyList(), channelId = Snowflake.min),
        json = """{"ids":[],"channel_id":"0"}""",
    )

    @Test
    fun test_MessageReactionAdd_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_REACTION_ADD",
        eventConstructor = ::MessageReactionAdd,
        data = MessageReactionAddData(
            userId = Snowflake.min,
            channelId = Snowflake.min,
            messageId = Snowflake.min,
            emoji = DiscordPartialEmoji(id = Snowflake.min),
        ),
        json = """{"user_id":"0","channel_id":"0","message_id":"0","emoji":{"id":"0"}}""",
    )

    @Test
    fun test_MessageReactionRemove_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_REACTION_REMOVE",
        eventConstructor = ::MessageReactionRemove,
        data = MessageReactionRemoveData(
            userId = Snowflake.min,
            channelId = Snowflake.min,
            messageId = Snowflake.min,
            emoji = DiscordPartialEmoji(name = "❤️"),
        ),
        json = """{"user_id":"0","channel_id":"0","message_id":"0","emoji":{"name":"❤️"}}""",
    )

    @Test
    fun test_MessageReactionRemoveAll_deserialization() = testDispatchEventDeserialization(
        eventName = "MESSAGE_REACTION_REMOVE_ALL",
        eventConstructor = ::MessageReactionRemoveAll,
        data = AllRemovedMessageReactions(channelId = Snowflake.min, messageId = Snowflake.min),
        json = """{"channel_id":"0","message_id":"0"}""",
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

    /*
     * Missing:
     * - StageInstanceCreate
     * - StageInstanceUpdate
     * - StageInstanceDelete
     */

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
    fun test_UserUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "USER_UPDATE",
        eventConstructor = ::UserUpdate,
        data = user,
        json = userJson,
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
    fun test_VoiceServerUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "VOICE_SERVER_UPDATE",
        eventConstructor = ::VoiceServerUpdate,
        data = DiscordVoiceServerUpdateData(token = "hunter2", guildId = Snowflake.min, endpoint = null),
        json = """{"token":"hunter2","guild_id":"0","endpoint":null}""",
    )

    @Test
    fun test_WebhooksUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "WEBHOOKS_UPDATE",
        eventConstructor = ::WebhooksUpdate,
        data = DiscordWebhooksUpdateData(guildId = Snowflake.min, channelId = Snowflake.min),
        json = """{"guild_id":"0","channel_id":"0"}""",
    )

    @Test
    fun test_UnknownDispatchEvent_deserialization() = testDispatchEventDeserialization(
        eventName = "SOME_UNKNOWN_EVENT",
        eventConstructor = { data, sequence -> UnknownDispatchEvent(name = "SOME_UNKNOWN_EVENT", data, sequence) },
        data = buildJsonObject { put("foo", "bar") },
        json = """{"foo":"bar"}""",
    )


    // The following events have been removed from Discord's documentation, we should probably remove them too.
    // See https://github.com/discord/discord-api-docs/pull/3691

    private val applicationCommand = DiscordApplicationCommand(
        id = Snowflake.min,
        applicationId = Snowflake.min,
        name = "name",
        description = null,
        defaultMemberPermissions = null,
        version = Snowflake.min,
    )
    private val applicationCommandJson = """{"id":"0","application_id":"0","name":"name","description":null,""" +
        """"default_member_permissions":null,"version":"0"}"""

    @Test
    @Suppress("DEPRECATION_ERROR")
    fun test_ApplicationCommandCreate_deserialization() = testDispatchEventDeserialization(
        eventName = "APPLICATION_COMMAND_CREATE",
        eventConstructor = ::ApplicationCommandCreate,
        data = applicationCommand,
        json = applicationCommandJson,
    )

    @Test
    @Suppress("DEPRECATION_ERROR")
    fun test_ApplicationCommandUpdate_deserialization() = testDispatchEventDeserialization(
        eventName = "APPLICATION_COMMAND_UPDATE",
        eventConstructor = ::ApplicationCommandUpdate,
        data = applicationCommand,
        json = applicationCommandJson,
    )

    @Test
    @Suppress("DEPRECATION_ERROR")
    fun test_ApplicationCommandDelete_deserialization() = testDispatchEventDeserialization(
        eventName = "APPLICATION_COMMAND_DELETE",
        eventConstructor = ::ApplicationCommandDelete,
        data = applicationCommand,
        json = applicationCommandJson,
    )


}
