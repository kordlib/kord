package dev.kord.gateway

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlin.jvm.JvmField
import kotlinx.serialization.DeserializationStrategy as KDeserializationStrategy

private val jsonLogger = KotlinLogging.logger { }

public sealed class DispatchEvent : Event() {
    public abstract val sequence: Int?
}

public sealed class Event {
    public object DeserializationStrategy : KDeserializationStrategy<Event> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("dev.kord.gateway.Event") {
            element("op", OpCode.serializer().descriptor)
            element("t", String.serializer().descriptor.nullable, isOptional = true)
            element("s", Int.serializer().descriptor.nullable, isOptional = true)
            element("d", JsonElement.serializer().descriptor, isOptional = true)
        }

        override fun deserialize(decoder: Decoder): Event = decoder.decodeStructure(descriptor) {
            var op: OpCode? = null
            var t: String? = null
            var s: Int? = null
            var d: JsonElement? = null
            while (true) {
                @OptIn(ExperimentalSerializationApi::class)
                when (val index = decodeElementIndex(descriptor)) {
                    0 -> op = decodeSerializableElement(descriptor, index, OpCode.serializer(), op)
                    1 -> t = decodeNullableSerializableElement(descriptor, index, String.serializer(), t)
                    2 -> s = decodeNullableSerializableElement(descriptor, index, Int.serializer(), s)
                    3 -> d = decodeSerializableElement(descriptor, index, JsonElement.serializer(), d)
                    CompositeDecoder.DECODE_DONE -> break
                    else -> throw SerializationException("Unexpected index: $index")
                }
            }
            when (op) {
                null ->
                    throw @OptIn(ExperimentalSerializationApi::class) MissingFieldException("op", descriptor.serialName)
                OpCode.Dispatch -> decodeDispatchEvent(decoder, eventName = t, sequence = s, eventData = d)
                OpCode.Heartbeat -> decodeNonDispatchEvent(decoder, op, Heartbeat.serializer(), eventData = d)
                OpCode.Reconnect -> {
                    // ignore the d field, Reconnect is supposed to have null here:
                    // https://discord.com/developers/docs/topics/gateway-events#reconnect
                    Reconnect
                }
                OpCode.InvalidSession -> decodeNonDispatchEvent(decoder, op, InvalidSession.serializer(), eventData = d)
                OpCode.Hello -> decodeNonDispatchEvent(decoder, op, Hello.serializer(), eventData = d)
                OpCode.HeartbeatACK -> {
                    // ignore the d field, Heartbeat ACK is supposed to omit it:
                    // https://discord.com/developers/docs/topics/gateway#heartbeat-interval-example-heartbeat-ack
                    HeartbeatACK
                }
                // OpCodes for Commands (aka send events), they shouldn't be received
                OpCode.Identify, OpCode.StatusUpdate, OpCode.VoiceStateUpdate, OpCode.Resume,
                OpCode.RequestGuildMembers,
                -> throw IllegalArgumentException("Illegal opcode for gateway event: $op")
                OpCode.Unknown -> throw IllegalArgumentException("Unknown opcode for gateway event")
            }
        }

        private fun <T> decodeNonDispatchEvent(
            decoder: Decoder,
            op: OpCode,
            deserializer: KDeserializationStrategy<T>,
            eventData: JsonElement?,
        ): T {
            requireNotNull(eventData) { "Gateway event is missing 'd' field for opcode $op" }
            // this cast will always succeed, otherwise decoder couldn't have decoded eventData
            return (decoder as JsonDecoder).json.decodeFromJsonElement(deserializer, eventData)
        }

        private fun decodeDispatchEvent(
            decoder: Decoder,
            eventName: String?,
            sequence: Int?,
            eventData: JsonElement?,
        ): DispatchEvent {
            fun <T> decode(deserializer: KDeserializationStrategy<T>): T {
                requireNotNull(eventData) { "Gateway event is missing 'd' field for event name $eventName" }
                // this cast will always succeed, otherwise decoder couldn't have decoded eventData
                return (decoder as JsonDecoder).json.decodeFromJsonElement(deserializer, eventData)
            }
            /*
             * Keep ordered like this table: https://discord.com/developers/docs/topics/gateway-events#receive-events
             * (Hello, Reconnect and Invalid Session are decoded above, they are no DispatchEvents)
             *
             * The names are sent in SCREAMING_SNAKE_CASE:
             * https://discord.com/developers/docs/topics/gateway-events#event-names
             */
            return when (eventName) {
                "READY" -> Ready(decode(ReadyData.serializer()), sequence)
                "RESUMED" -> {
                    // ignore the d field, the content isn't documented:
                    // https://discord.com/developers/docs/topics/gateway-events#resumed
                    Resumed(sequence)
                }
                "APPLICATION_COMMAND_PERMISSIONS_UPDATE" -> ApplicationCommandPermissionsUpdate(
                    decode(DiscordGuildApplicationCommandPermissions.serializer()), sequence,
                )
                "AUTO_MODERATION_RULE_CREATE" ->
                    AutoModerationRuleCreate(decode(DiscordAutoModerationRule.serializer()), sequence)
                "AUTO_MODERATION_RULE_UPDATE" ->
                    AutoModerationRuleUpdate(decode(DiscordAutoModerationRule.serializer()), sequence)
                "AUTO_MODERATION_RULE_DELETE" ->
                    AutoModerationRuleDelete(decode(DiscordAutoModerationRule.serializer()), sequence)
                "AUTO_MODERATION_ACTION_EXECUTION" ->
                    AutoModerationActionExecution(decode(DiscordAutoModerationActionExecution.serializer()), sequence)
                "CHANNEL_CREATE" -> ChannelCreate(decode(DiscordChannel.serializer()), sequence)
                "CHANNEL_UPDATE" -> ChannelUpdate(decode(DiscordChannel.serializer()), sequence)
                "CHANNEL_DELETE" -> ChannelDelete(decode(DiscordChannel.serializer()), sequence)
                "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(decode(DiscordPinsUpdateData.serializer()), sequence)
                "THREAD_CREATE" -> ThreadCreate(decode(DiscordChannel.serializer()), sequence)
                "THREAD_UPDATE" -> ThreadUpdate(decode(DiscordChannel.serializer()), sequence)
                "THREAD_DELETE" -> ThreadDelete(decode(DiscordChannel.serializer()), sequence)
                "THREAD_LIST_SYNC" -> ThreadListSync(decode(DiscordThreadListSync.serializer()), sequence)
                "THREAD_MEMBER_UPDATE" -> ThreadMemberUpdate(decode(DiscordThreadMember.serializer()), sequence)
                "THREAD_MEMBERS_UPDATE" ->
                    ThreadMembersUpdate(decode(DiscordThreadMembersUpdate.serializer()), sequence)
                "ENTITLEMENT_CREATE" -> EntitlementCreate(decode(DiscordEntitlement.serializer()), sequence)
                "ENTITLEMENT_UPDATE" -> EntitlementUpdate(decode(DiscordEntitlement.serializer()), sequence)
                "ENTITLEMENT_DELETE" -> EntitlementDelete(decode(DiscordEntitlement.serializer()), sequence)
                "GUILD_CREATE" -> GuildCreate(decode(DiscordGuild.serializer()), sequence)
                "GUILD_UPDATE" -> GuildUpdate(decode(DiscordGuild.serializer()), sequence)
                "GUILD_DELETE" -> GuildDelete(decode(DiscordUnavailableGuild.serializer()), sequence)
                "GUILD_AUDIT_LOG_ENTRY_CREATE" ->
                    GuildAuditLogEntryCreate(decode(DiscordAuditLogEntry.serializer()), sequence)
                "GUILD_BAN_ADD" -> GuildBanAdd(decode(DiscordGuildBan.serializer()), sequence)
                "GUILD_BAN_REMOVE" -> GuildBanRemove(decode(DiscordGuildBan.serializer()), sequence)
                "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(decode(DiscordUpdatedEmojis.serializer()), sequence)
                // Missing: GuildStickers Update
                "GUILD_INTEGRATIONS_UPDATE" ->
                    GuildIntegrationsUpdate(decode(DiscordGuildIntegrations.serializer()), sequence)
                "GUILD_MEMBER_ADD" -> GuildMemberAdd(decode(DiscordAddedGuildMember.serializer()), sequence)
                "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(decode(DiscordRemovedGuildMember.serializer()), sequence)
                "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(decode(DiscordUpdatedGuildMember.serializer()), sequence)
                "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(decode(GuildMembersChunkData.serializer()), sequence)
                "GUILD_ROLE_CREATE" -> GuildRoleCreate(decode(DiscordGuildRole.serializer()), sequence)
                "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(decode(DiscordGuildRole.serializer()), sequence)
                "GUILD_ROLE_DELETE" -> GuildRoleDelete(decode(DiscordDeletedGuildRole.serializer()), sequence)
                "GUILD_SCHEDULED_EVENT_CREATE" ->
                    GuildScheduledEventCreate(decode(DiscordGuildScheduledEvent.serializer()), sequence)
                "GUILD_SCHEDULED_EVENT_UPDATE" ->
                    GuildScheduledEventUpdate(decode(DiscordGuildScheduledEvent.serializer()), sequence)
                "GUILD_SCHEDULED_EVENT_DELETE" ->
                    GuildScheduledEventDelete(decode(DiscordGuildScheduledEvent.serializer()), sequence)
                "GUILD_SCHEDULED_EVENT_USER_ADD" ->
                    GuildScheduledEventUserAdd(decode(GuildScheduledEventUserMetadata.serializer()), sequence)
                "GUILD_SCHEDULED_EVENT_USER_REMOVE" ->
                    GuildScheduledEventUserRemove(decode(GuildScheduledEventUserMetadata.serializer()), sequence)
                "INTEGRATION_CREATE" -> IntegrationCreate(decode(DiscordIntegration.serializer()), sequence)
                "INTEGRATION_UPDATE" -> IntegrationUpdate(decode(DiscordIntegration.serializer()), sequence)
                "INTEGRATION_DELETE" -> IntegrationDelete(decode(DiscordIntegrationDelete.serializer()), sequence)
                "INTERACTION_CREATE" -> InteractionCreate(decode(DiscordInteraction.serializer()), sequence)
                "INVITE_CREATE" -> InviteCreate(decode(DiscordCreatedInvite.serializer()), sequence)
                "INVITE_DELETE" -> InviteDelete(decode(DiscordDeletedInvite.serializer()), sequence)
                "MESSAGE_CREATE" -> MessageCreate(decode(DiscordMessage.serializer()), sequence)
                "MESSAGE_UPDATE" -> MessageUpdate(decode(DiscordPartialMessage.serializer()), sequence)
                "MESSAGE_DELETE" -> MessageDelete(decode(DeletedMessage.serializer()), sequence)
                "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(decode(BulkDeleteData.serializer()), sequence)
                "MESSAGE_REACTION_ADD" -> MessageReactionAdd(decode(MessageReactionAddData.serializer()), sequence)
                "MESSAGE_REACTION_REMOVE" ->
                    MessageReactionRemove(decode(MessageReactionRemoveData.serializer()), sequence)
                "MESSAGE_REACTION_REMOVE_ALL" ->
                    MessageReactionRemoveAll(decode(AllRemovedMessageReactions.serializer()), sequence)
                "MESSAGE_REACTION_REMOVE_EMOJI" ->
                    MessageReactionRemoveEmoji(decode(DiscordRemovedEmoji.serializer()), sequence)
                "PRESENCE_UPDATE" -> PresenceUpdate(decode(DiscordPresenceUpdate.serializer()), sequence)
                //  Missing: Stage Instance Create, Stage Instance Update, Stage Instance Delete
                "TYPING_START" -> TypingStart(decode(DiscordTyping.serializer()), sequence)
                "USER_UPDATE" -> UserUpdate(decode(DiscordUser.serializer()), sequence)
                "VOICE_STATE_UPDATE" -> VoiceStateUpdate(decode(DiscordVoiceState.serializer()), sequence)
                "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(decode(DiscordVoiceServerUpdateData.serializer()), sequence)
                "WEBHOOKS_UPDATE" -> WebhooksUpdate(decode(DiscordWebhooksUpdateData.serializer()), sequence)
                // The following three events have been removed from Discord's documentation, we should probably remove
                // them too.
                // See https://github.com/discord/discord-api-docs/pull/3691
                "APPLICATION_COMMAND_CREATE" ->
                    @Suppress("DEPRECATION_ERROR")
                    ApplicationCommandCreate(decode(DiscordApplicationCommand.serializer()), sequence)
                "APPLICATION_COMMAND_UPDATE" ->
                    @Suppress("DEPRECATION_ERROR")
                    ApplicationCommandUpdate(decode(DiscordApplicationCommand.serializer()), sequence)
                "APPLICATION_COMMAND_DELETE" ->
                    @Suppress("DEPRECATION_ERROR")
                    ApplicationCommandDelete(decode(DiscordApplicationCommand.serializer()), sequence)
                else -> {
                    jsonLogger.debug { "Unknown gateway event name: $eventName" }
                    UnknownDispatchEvent(eventName, eventData, sequence)
                }
            }
        }
    }
}


public sealed class Close : Event() {

    /**
     * The Gateway was detached, all resources tied to the gateway should be freed.
     */
    public object Detach : Close()

    /**
     * The user closed the Gateway connection.
     */
    public object UserClose : Close()

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    public object Timeout : Close()

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    public data class DiscordClose(val closeCode: GatewayCloseCode, val recoverable: Boolean) : Close()

    /**
     * The gateway closed and will attempt to resume the session.
     */
    public object Reconnecting : Close()

    /**
     * The gateway closed and will attempt to start a new session.
     */
    public object SessionReset : Close()

    /**
     * Discord is no longer responding to the gateway commands, the connection will be closed and an attempt to resume the session will be made.
     * Any [commands][Command] send recently might not complete, and won't be automatically requeued.
     */
    public object ZombieConnection : Close()

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [Gateway.start], otherwise all resources linked to the Gateway should free and the Gateway [detached][Gateway.detach].
     */
    public object RetryLimitReached : Close()
}

public object HeartbeatACK : Event()
public object Reconnect : Event()

@Serializable
public data class Hello(
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Int,
) : Event()

public data class Ready(val data: ReadyData, override val sequence: Int?) : DispatchEvent()

@Serializable
public data class ReadyData(
    @SerialName("v")
    val version: Int,
    val user: DiscordUser,
    @SerialName("private_channels")
    val privateChannels: List<DiscordChannel>,
    val guilds: List<DiscordUnavailableGuild>,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("resume_gateway_url")
    val resumeGatewayUrl: String,
    @SerialName("geo_ordered_rtc_regions")
    val geoOrderedRtcRegions: Optional<JsonElement?> = Optional.Missing(),
    @SerialName("guild_hashes")
    val guildHashes: Optional<JsonElement?> = Optional.Missing(),
    val application: Optional<JsonElement?> = Optional.Missing(),
    @SerialName("_trace")
    val traces: List<String>,
    val shard: Optional<DiscordShard> = Optional.Missing(),
)

@Serializable(with = Heartbeat.Serializer::class)
public data class Heartbeat(val data: Long) : Event() {
    internal object Serializer : KSerializer<Heartbeat> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.gateway.Heartbeat", PrimitiveKind.LONG)
        override fun serialize(encoder: Encoder, value: Heartbeat) = encoder.encodeLong(value.data)
        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
    }

    public companion object {
        @Suppress("DEPRECATION_ERROR")
        @Deprecated(
            "Renamed to 'Companion'.",
            ReplaceWith("Heartbeat.Companion", imports = ["dev.kord.gateway.Heartbeat"]),
            DeprecationLevel.ERROR,
        )
        @JvmField
        public val NewCompanion: NewCompanion = NewCompanion()
    }

    @Deprecated(
        "Renamed to 'Companion'.",
        ReplaceWith("Heartbeat.Companion", imports = ["dev.kord.gateway.Heartbeat"]),
        DeprecationLevel.ERROR,
    )
    public class NewCompanion internal constructor() {
        public fun serializer(): KSerializer<Heartbeat> = Heartbeat.serializer()
    }
}

@Serializable
public data class Resumed(
    override val sequence: Int?,
) : DispatchEvent()

@Serializable(with = InvalidSession.Serializer::class)
public data class InvalidSession(val resumable: Boolean) : Event() {

    internal object Serializer : KSerializer<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.InvalidSession", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean())

        override fun serialize(encoder: Encoder, value: InvalidSession) {
            encoder.encodeBoolean(value.resumable)
        }
    }
}

public data class ApplicationCommandPermissionsUpdate(
    val permissions: DiscordGuildApplicationCommandPermissions,
    override val sequence: Int?
) : DispatchEvent()

public data class AutoModerationRuleCreate(val rule: DiscordAutoModerationRule, override val sequence: Int?) :
    DispatchEvent()

public data class AutoModerationRuleUpdate(val rule: DiscordAutoModerationRule, override val sequence: Int?) :
    DispatchEvent()

public data class AutoModerationRuleDelete(val rule: DiscordAutoModerationRule, override val sequence: Int?) :
    DispatchEvent()

public data class AutoModerationActionExecution(
    val actionExecution: DiscordAutoModerationActionExecution,
    override val sequence: Int?,
) : DispatchEvent()

@Serializable
public data class DiscordAutoModerationActionExecution(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val action: DiscordAutoModerationAction,
    @SerialName("rule_id")
    val ruleId: Snowflake,
    @SerialName("rule_trigger_type")
    val ruleTriggerType: AutoModerationRuleTriggerType,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("channel_id")
    val channelId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("message_id")
    val messageId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("alert_system_message_id")
    val alertSystemMessageId: OptionalSnowflake = OptionalSnowflake.Missing,
    val content: String,
    @SerialName("matched_keyword")
    val matchedKeyword: String?,
    @SerialName("matched_content")
    val matchedContent: String?,
)

public data class ChannelCreate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()
public data class ChannelUpdate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()
public data class ChannelDelete(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()
public data class ChannelPinsUpdate(val pins: DiscordPinsUpdateData, override val sequence: Int?) : DispatchEvent()

public data class TypingStart(val data: DiscordTyping, override val sequence: Int?) : DispatchEvent()
public data class GuildAuditLogEntryCreate(val entry: DiscordAuditLogEntry, override val sequence: Int?): DispatchEvent()
public data class GuildCreate(val guild: DiscordGuild, override val sequence: Int?) : DispatchEvent()
public data class GuildUpdate(val guild: DiscordGuild, override val sequence: Int?) : DispatchEvent()
public data class GuildDelete(val guild: DiscordUnavailableGuild, override val sequence: Int?) : DispatchEvent()
public data class GuildBanAdd(val ban: DiscordGuildBan, override val sequence: Int?) : DispatchEvent()
public data class GuildBanRemove(val ban: DiscordGuildBan, override val sequence: Int?) : DispatchEvent()
public data class GuildEmojisUpdate(val emoji: DiscordUpdatedEmojis, override val sequence: Int?) : DispatchEvent()
public data class GuildIntegrationsUpdate(val integrations: DiscordGuildIntegrations, override val sequence: Int?) :
    DispatchEvent()
public data class IntegrationDelete(val integration: DiscordIntegrationDelete, override val sequence: Int?) :
    DispatchEvent()
public data class IntegrationCreate(val integration: DiscordIntegration, override val sequence: Int?) :
    DispatchEvent()
public data class IntegrationUpdate(val integration: DiscordIntegration, override val sequence: Int?) :
    DispatchEvent()

public data class GuildMemberAdd(val member: DiscordAddedGuildMember, override val sequence: Int?) : DispatchEvent()
public data class GuildMemberRemove(val member: DiscordRemovedGuildMember, override val sequence: Int?) :
    DispatchEvent()

public data class GuildMemberUpdate(val member: DiscordUpdatedGuildMember, override val sequence: Int?) :
    DispatchEvent()

public data class GuildRoleCreate(val role: DiscordGuildRole, override val sequence: Int?) : DispatchEvent()
public data class GuildRoleUpdate(val role: DiscordGuildRole, override val sequence: Int?) : DispatchEvent()
public data class GuildRoleDelete(val role: DiscordDeletedGuildRole, override val sequence: Int?) : DispatchEvent()
public data class GuildMembersChunk(val data: GuildMembersChunkData, override val sequence: Int?) : DispatchEvent()

/**
 * Sent when a new invite to a channel is created.
 */
public data class InviteCreate(val invite: DiscordCreatedInvite, override val sequence: Int?) : DispatchEvent()

/**
 * Sent when an invite is deleted.
 */
public data class InviteDelete(val invite: DiscordDeletedInvite, override val sequence: Int?) : DispatchEvent()

@Serializable
public data class DiscordDeletedInvite(
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val code: String,
)

@Serializable
public data class DiscordCreatedInvite(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val code: String,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val inviter: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("max_age")
    val maxAge: DurationInSeconds,
    @SerialName("max_uses")
    val maxUses: Int,
    @SerialName("target_type")
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    @SerialName("target_user")
    val targetUser: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("target_application")
    val targetApplication: Optional<DiscordPartialApplication> = Optional.Missing(),
    val temporary: Boolean,
    val uses: Int,
)

public data class MessageCreate(val message: DiscordMessage, override val sequence: Int?) : DispatchEvent()
public data class MessageUpdate(val message: DiscordPartialMessage, override val sequence: Int?) : DispatchEvent()
public data class MessageDelete(val message: DeletedMessage, override val sequence: Int?) : DispatchEvent()
public data class MessageDeleteBulk(val messageBulk: BulkDeleteData, override val sequence: Int?) : DispatchEvent()
public data class MessageReactionAdd(val reaction: MessageReactionAddData, override val sequence: Int?) :
    DispatchEvent()

public data class MessageReactionRemove(val reaction: MessageReactionRemoveData, override val sequence: Int?) :
    DispatchEvent()

public data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions, override val sequence: Int?) :
    DispatchEvent()

public data class MessageReactionRemoveEmoji(val reaction: DiscordRemovedEmoji, override val sequence: Int?) :
    DispatchEvent()

@Serializable
public data class DiscordRemovedEmoji(
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("message_id")
    val messageId: Snowflake,
    val emoji: DiscordRemovedReactionEmoji,
)

@Serializable
public data class DiscordRemovedReactionEmoji(
    val id: Snowflake?,
    val name: String?,
)

public data class PresenceUpdate(val presence: DiscordPresenceUpdate, override val sequence: Int?) : DispatchEvent()
public data class UserUpdate(val user: DiscordUser, override val sequence: Int?) : DispatchEvent()
public data class VoiceStateUpdate(val voiceState: DiscordVoiceState, override val sequence: Int?) : DispatchEvent()
public data class VoiceServerUpdate(
    val voiceServerUpdateData: DiscordVoiceServerUpdateData,
    override val sequence: Int?,
) : DispatchEvent()

public data class WebhooksUpdate(val webhooksUpdateData: DiscordWebhooksUpdateData, override val sequence: Int?) :
    DispatchEvent()


public data class InteractionCreate(val interaction: DiscordInteraction, override val sequence: Int?) : DispatchEvent()


@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public data class ApplicationCommandCreate(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()


@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public data class ApplicationCommandUpdate(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()


@Deprecated(
    "This event is not supposed to be sent to bots. See https://github.com/discord/discord-api-docs/issues/3690 for " +
        "details.",
    level = DeprecationLevel.ERROR,
)
public data class ApplicationCommandDelete(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()

public data class ThreadCreate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()

public data class ThreadUpdate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()

public data class ThreadDelete(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()

public data class ThreadMemberUpdate(val member: DiscordThreadMember, override val sequence: Int?) : DispatchEvent()

public data class ThreadListSync(val sync: DiscordThreadListSync, override val sequence: Int?) : DispatchEvent()

public data class ThreadMembersUpdate(val members: DiscordThreadMembersUpdate, override val sequence: Int?) :
    DispatchEvent()

public data class GuildScheduledEventCreate(val event: DiscordGuildScheduledEvent, override val sequence: Int?) :
    DispatchEvent()

public data class GuildScheduledEventUpdate(val event: DiscordGuildScheduledEvent, override val sequence: Int?) :
    DispatchEvent()

public data class GuildScheduledEventDelete(val event: DiscordGuildScheduledEvent, override val sequence: Int?) :
    DispatchEvent()

public data class GuildScheduledEventUserAdd(
    val data: GuildScheduledEventUserMetadata,
    override val sequence: Int?,
) : DispatchEvent()

public data class GuildScheduledEventUserRemove(
    val data: GuildScheduledEventUserMetadata,
    override val sequence: Int?,
) : DispatchEvent()

public data class UnknownDispatchEvent(
    val name: String?,
    val data: JsonElement?,
    override val sequence: Int?
) : DispatchEvent()

@Serializable
public data class GuildScheduledEventUserMetadata(
    @SerialName("guild_scheduled_event_id")
    val guildScheduledEventId: Snowflake,
    @SerialName("user_id")
    val userId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
)

@Serializable
public data class DiscordThreadListSync(
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_ids")
    val channelIds: Optional<List<Snowflake>> = Optional.Missing(),
    val threads: List<DiscordChannel>,
    val members: List<DiscordThreadMember>
)

@Serializable
public data class DiscordThreadMembersUpdate(
    val id: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("member_count")
    val memberCount: Int,
    @SerialName("added_members")
    val addedMembers: Optional<List<DiscordThreadMember>> = Optional.Missing(),
    @SerialName("removed_member_ids")
    val removedMemberIds: Optional<List<Snowflake>> = Optional.Missing()
)

public data class EntitlementCreate(val entitlement: DiscordEntitlement, override val sequence: Int?) : DispatchEvent()

public data class EntitlementUpdate(val entitlement: DiscordEntitlement, override val sequence: Int?) : DispatchEvent()

public data class EntitlementDelete(val entitlement: DiscordEntitlement, override val sequence: Int?) : DispatchEvent()
