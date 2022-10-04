package dev.kord.gateway

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging
import kotlin.DeprecationLevel.HIDDEN
import kotlinx.serialization.DeserializationStrategy as KDeserializationStrategy

private val jsonLogger = KotlinLogging.logger { }

public sealed class DispatchEvent : Event() {
    public abstract val sequence: Int?
}

private object NullDecoder : KDeserializationStrategy<Nothing?> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("null", PrimitiveKind.STRING)

    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): Nothing? {
        // decodeNull() doesn't consume the literal null therefore parsing doesn't end and in e.g. a heartbeat event
        // the null gets parsed as a key
        decoder.decodeNotNullMark()
        return decoder.decodeNull()
    }

}

public sealed class Event {
    public object DeserializationStrategy : KDeserializationStrategy<Event?> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
            element("op", OpCode.serializer().descriptor)
            element("t", String.serializer().descriptor, isOptional = true)
            element("s", Int.serializer().descriptor, isOptional = true)
            element("d", JsonObject.serializer().descriptor, isOptional = true)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Event? {
            var op: OpCode? = null
            var data: Event? = null
            var sequence: Int? = null
            var eventName: String? = null

            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index =
                        decodeElementIndex(descriptor)) {//we assume the all fields to be present *before* the data field
                        CompositeDecoder.DECODE_DONE -> break@loop
                        0 -> {
                            op = decodeSerializableElement(descriptor, index, OpCode.serializer())
                            when (op) {
                                OpCode.HeartbeatACK -> data = HeartbeatACK
                                OpCode.Reconnect -> data = Reconnect
                                else -> {}
                            }
                        }
                        1 -> eventName =
                            decodeNullableSerializableElement(descriptor, index, String.serializer().nullable)
                        2 -> sequence = decodeNullableSerializableElement(descriptor, index, Int.serializer().nullable)
                        3 -> data = when (op) {
                            OpCode.Dispatch -> getByDispatchEvent(index, this, eventName, sequence)
                            OpCode.Heartbeat -> decodeSerializableElement(descriptor, index, Heartbeat.serializer())
                            OpCode.HeartbeatACK -> {
                                this.decodeSerializableElement(descriptor, index, NullDecoder)
                                HeartbeatACK
                            }
                            OpCode.InvalidSession -> decodeSerializableElement(
                                descriptor,
                                index,
                                InvalidSession.serializer()
                            )
                            OpCode.Hello -> decodeSerializableElement(descriptor, index, Hello.serializer())
                            //some events contain undocumented data fields, we'll only assume an unknown opcode with no data to be an error
                            else -> if (data == null) {
                                val element = decodeNullableSerializableElement(
                                    descriptor,
                                    index,
                                    JsonElement.serializer().nullable
                                )
                                error("Unknown 'd' field for Op code ${op?.code}: $element")
                            } else {
                                decodeNullableSerializableElement(descriptor, index, JsonElement.serializer().nullable)
                                data
                            }
                        }
                    }
                }
                endStructure(descriptor)
                if (op == OpCode.Dispatch && eventName == "RESUMED") return Resumed(sequence)
                return data
            }
        }


        @OptIn(ExperimentalSerializationApi::class)
        private fun getByDispatchEvent(index: Int, decoder: CompositeDecoder, name: String?, sequence: Int?) =
            when (name) {
                "PRESENCES_REPLACE" -> {
                    decoder.decodeNullableSerializableElement(descriptor, index, JsonElement.serializer().nullable)
                    null //https://github.com/kordlib/kord/issues/42
                }
                "RESUMED" -> {
                    decoder.decodeNullableSerializableElement(descriptor, index, JsonElement.serializer().nullable)
                    Resumed(sequence)
                }
                "READY" -> Ready(decoder.decodeSerializableElement(descriptor, index, ReadyData.serializer()), sequence)
                "APPLICATION_COMMAND_PERMISSIONS_UPDATE" -> ApplicationCommandPermissionsUpdate(
                    decoder.decodeSerializableElement(
                        descriptor, index, DiscordGuildApplicationCommandPermissions.serializer()
                    ), sequence
                )
                "AUTO_MODERATION_RULE_CREATE" -> AutoModerationRuleCreate(
                    rule = decoder.decodeSerializableElement(descriptor, index, DiscordAutoModerationRule.serializer()),
                    sequence,
                )
                "AUTO_MODERATION_RULE_UPDATE" -> AutoModerationRuleUpdate(
                    rule = decoder.decodeSerializableElement(descriptor, index, DiscordAutoModerationRule.serializer()),
                    sequence,
                )
                "AUTO_MODERATION_RULE_DELETE" -> AutoModerationRuleDelete(
                    rule = decoder.decodeSerializableElement(descriptor, index, DiscordAutoModerationRule.serializer()),
                    sequence,
                )
                "AUTO_MODERATION_ACTION_EXECUTION" -> AutoModerationActionExecution(
                    actionExecution = decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordAutoModerationActionExecution.serializer(),
                    ),
                    sequence,
                )
                "CHANNEL_CREATE" -> ChannelCreate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordChannel.serializer()
                    ), sequence
                )
                "CHANNEL_UPDATE" -> ChannelUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordChannel.serializer()
                    ), sequence
                )
                "CHANNEL_DELETE" -> ChannelDelete(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordChannel.serializer()
                    ), sequence
                )
                "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordPinsUpdateData.serializer()
                    ), sequence
                )
                "TYPING_START" -> TypingStart(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordTyping.serializer()
                    ), sequence
                )
                "GUILD_CREATE" -> GuildCreate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuild.serializer()
                    ), sequence
                )
                "GUILD_UPDATE" -> GuildUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuild.serializer()
                    ), sequence
                )
                "GUILD_DELETE" -> GuildDelete(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordUnavailableGuild.serializer()
                    ), sequence
                )
                "GUILD_BAN_ADD" -> GuildBanAdd(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuildBan.serializer()
                    ), sequence
                )
                "GUILD_BAN_REMOVE" -> GuildBanRemove(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuildBan.serializer()
                    ), sequence
                )
                "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordUpdatedEmojis.serializer()
                    ), sequence
                )
                "GUILD_INTEGRATIONS_UPDATE" -> GuildIntegrationsUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuildIntegrations.serializer()
                    ), sequence
                )
                "GUILD_MEMBER_ADD" -> GuildMemberAdd(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordAddedGuildMember.serializer()
                    ), sequence
                )
                "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordRemovedGuildMember.serializer()
                    ), sequence
                )
                "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordUpdatedGuildMember.serializer()
                    ), sequence
                )
                "GUILD_ROLE_CREATE" -> GuildRoleCreate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuildRole.serializer()
                    ), sequence
                )
                "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordGuildRole.serializer()
                    ), sequence
                )
                "GUILD_ROLE_DELETE" -> GuildRoleDelete(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordDeletedGuildRole.serializer()
                    ), sequence
                )
                "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        GuildMembersChunkData.serializer()
                    ), sequence
                )

                "INVITE_CREATE" -> InviteCreate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordCreatedInvite.serializer()
                    ), sequence
                )
                "INVITE_DELETE" -> InviteDelete(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordDeletedInvite.serializer()
                    ), sequence
                )

                "MESSAGE_CREATE" -> MessageCreate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordMessage.serializer()
                    ), sequence
                )
                "MESSAGE_UPDATE" -> MessageUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordPartialMessage.serializer()
                    ), sequence
                )
                "MESSAGE_DELETE" -> MessageDelete(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DeletedMessage.serializer()
                    ), sequence
                )
                "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        BulkDeleteData.serializer()
                    ), sequence
                )
                "MESSAGE_REACTION_ADD" -> MessageReactionAdd(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        MessageReactionAddData.serializer()
                    ), sequence
                )
                "MESSAGE_REACTION_REMOVE" -> MessageReactionRemove(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        MessageReactionRemoveData.serializer()
                    ), sequence
                )
                "MESSAGE_REACTION_REMOVE_EMOJI" -> MessageReactionRemoveEmoji(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordRemovedEmoji.serializer()
                    ), sequence
                )

                "MESSAGE_REACTION_REMOVE_ALL" -> MessageReactionRemoveAll(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        AllRemovedMessageReactions.serializer()
                    ), sequence
                )
                "PRESENCE_UPDATE" -> PresenceUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordPresenceUpdate.serializer()
                    ), sequence
                )
                "USER_UPDATE" -> UserUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordUser.serializer()
                    ), sequence
                )
                "VOICE_STATE_UPDATE" -> VoiceStateUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordVoiceState.serializer()
                    ), sequence
                )
                "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordVoiceServerUpdateData.serializer()
                    ), sequence
                )
                "WEBHOOKS_UPDATE" -> WebhooksUpdate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordWebhooksUpdateData.serializer()
                    ), sequence
                )
                "INTERACTION_CREATE" -> InteractionCreate(
                    decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        DiscordInteraction.serializer()
                    ), sequence
                )
                "APPLICATION_COMMAND_CREATE" -> ApplicationCommandCreate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordApplicationCommand.serializer()),
                    sequence
                )

                "APPLICATION_COMMAND_UPDATE" -> ApplicationCommandUpdate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordApplicationCommand.serializer()),
                    sequence
                )

                "APPLICATION_COMMAND_DELETE" -> ApplicationCommandDelete(
                    decoder.decodeSerializableElement(descriptor, index, DiscordApplicationCommand.serializer()),
                    sequence
                )

                "THREAD_CREATE" -> ThreadCreate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordChannel.serializer()),
                    sequence
                )


                "THREAD_DELETE" -> ThreadDelete(
                    decoder.decodeSerializableElement(descriptor, index, DiscordChannel.serializer()),
                    sequence
                )


                "THREAD_UPDATE" -> ThreadUpdate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordChannel.serializer()),
                    sequence
                )

                "THREAD_LIST_SYNC" -> ThreadListSync(
                    decoder.decodeSerializableElement(descriptor, index, DiscordThreadListSync.serializer()),
                    sequence
                )

                "THREAD_MEMBER_UPDATE" -> ThreadMemberUpdate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordThreadMember.serializer()),
                    sequence
                )


                "THREAD_MEMBERS_UPDATE" -> ThreadMembersUpdate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordThreadMembersUpdate.serializer()),
                    sequence
                )

                "GUILD_SCHEDULED_EVENT_CREATE" -> GuildScheduledEventCreate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordGuildScheduledEvent.serializer()),
                    sequence
                )
                "GUILD_SCHEDULED_EVENT_UPDATE" -> GuildScheduledEventUpdate(
                    decoder.decodeSerializableElement(descriptor, index, DiscordGuildScheduledEvent.serializer()),
                    sequence
                )
                "GUILD_SCHEDULED_EVENT_DELETE" -> GuildScheduledEventDelete(
                    decoder.decodeSerializableElement(descriptor, index, DiscordGuildScheduledEvent.serializer()),
                    sequence
                )
                "GUILD_SCHEDULED_EVENT_USER_ADD" -> GuildScheduledEventUserAdd(
                    data = decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        GuildScheduledEventUserMetadata.serializer(),
                    ),
                    sequence
                )
                "GUILD_SCHEDULED_EVENT_USER_REMOVE" -> GuildScheduledEventUserRemove(
                    data = decoder.decodeSerializableElement(
                        descriptor,
                        index,
                        GuildScheduledEventUserMetadata.serializer(),
                    ),
                    sequence
                )


                else -> {
                    jsonLogger.warn { "unknown gateway event name $name" }
                    // consume json elements that are unknown to us
                    decoder.decodeSerializableElement(descriptor, index, JsonElement.serializer().nullable)
                    null
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

@Serializable(with = Heartbeat.Companion::class)
public data class Heartbeat(val data: Long) : Event() {
    public companion object : KSerializer<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("HeartbeatEvent", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder): Heartbeat = Heartbeat(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: Heartbeat) {
            encoder.encodeLong(value.data)
        }
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
public data class GuildCreate(val guild: DiscordGuild, override val sequence: Int?) : DispatchEvent()
public data class GuildUpdate(val guild: DiscordGuild, override val sequence: Int?) : DispatchEvent()
public data class GuildDelete(val guild: DiscordUnavailableGuild, override val sequence: Int?) : DispatchEvent()
public data class GuildBanAdd(val ban: DiscordGuildBan, override val sequence: Int?) : DispatchEvent()
public data class GuildBanRemove(val ban: DiscordGuildBan, override val sequence: Int?) : DispatchEvent()
public data class GuildEmojisUpdate(val emoji: DiscordUpdatedEmojis, override val sequence: Int?) : DispatchEvent()
public data class GuildIntegrationsUpdate(val integrations: DiscordGuildIntegrations, override val sequence: Int?) :
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
    /** @suppress */
    @Deprecated("No longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"), level = HIDDEN)
    @SerialName("target_user_type")
    val targetUserType: Optional<@Suppress("DEPRECATION_ERROR") dev.kord.common.entity.TargetUserType> = Optional.Missing(),
    val temporary: Boolean,
    val uses: Int,
)

/** @suppress */
@Deprecated(
    "Use 'DiscordUser' instead, All missing fields have defaults.",
    ReplaceWith("DiscordUser", "dev.kord.common.entity.DiscordUser"),
    level = HIDDEN,
)
@Serializable
public data class DiscordInviteUser(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("public_flags")
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
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


public data class ApplicationCommandCreate(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()


public data class ApplicationCommandUpdate(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()


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
