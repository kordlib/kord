package dev.kord.gateway

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging

private val jsonLogger = KotlinLogging.logger { }

sealed class DispatchEvent : Event() {
    abstract val sequence: Int?
}

private object NullDecoder : DeserializationStrategy<Nothing?> {
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

sealed class Event {
    companion object : DeserializationStrategy<Event?> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Event") {
            element("op", OpCode.descriptor)
            element("t", String.serializer().descriptor, isOptional = true)
            element("s", Int.serializer().descriptor, isOptional = true)
            element("d", JsonObject.serializer().descriptor, isOptional = true)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override fun deserialize(decoder: Decoder): Event? {
            var op: OpCode? = null
            var data: Event? = null

            @Suppress("UNUSED_VARIABLE")
            var sequence: Int? = null //this isn't actually unused but seems to be a compiler bug

            @Suppress("UNUSED_VARIABLE")
            var eventName: String? = null //this isn't actually unused but seems to be a compiler bug
            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index =
                        decodeElementIndex(descriptor)) {//we assume the all fields to be present *before* the data field
                        CompositeDecoder.DECODE_DONE -> break@loop
                        0 -> {
                            op = OpCode.deserialize(decoder)
                            @Suppress("NON_EXHAUSTIVE_WHEN")
                            when (op) {
                                OpCode.HeartbeatACK -> data = HeartbeatACK
                                OpCode.Reconnect -> data = Reconnect
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
                                InvalidSession.Serializer
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



                else -> {
                    jsonLogger.warn { "unknown gateway event name $name" }
                    // consume json elements that are unknown to us
                    decoder.decodeSerializableElement(descriptor, index, JsonElement.serializer().nullable)
                    null
                }
            }

    }

}


sealed class Close : Event() {

    /**
     * The Gateway was detached, all resources tied to the gateway should be freed.
     */
    object Detach : Close()

    /**
     * The user closed the Gateway connection.
     */
    object UserClose : Close()

    /**
     * The connection was closed because of a timeout, probably due to a loss of internet connection.
     */
    object Timeout : Close()

    /**
     * Discord closed the connection with a [closeCode].
     *
     * @param recoverable true if the gateway will automatically try to reconnect.
     */
    class DiscordClose(val closeCode: GatewayCloseCode, val recoverable: Boolean) : Close()

    /**
     * The gateway closed and will attempt to resume the session.
     */
    object Reconnecting : Close()

    /**
     * The gateway closed and will attempt to start a new session.
     */
    object SessionReset : Close()

    /**
     * Discord is no longer responding to the gateway commands, the connection will be closed and an attempt to resume the session will be made.
     * Any [commands][Command] send recently might not complete, and won't be automatically requeued.
     */
    object ZombieConnection : Close()

    /**
     *  The Gateway has failed to establish a connection too many times and will not try to reconnect anymore.
     *  The user is free to manually connect again using [Gateway.start], otherwise all resources linked to the Gateway should free and the Gateway [detached][Gateway.detach].
     */
    object RetryLimitReached : Close()
}

object HeartbeatACK : Event()
object Reconnect : Event()

@Serializable
data class Hello(
    @SerialName("heartbeat_interval")
    val heartbeatInterval: Int,
) : Event()

data class Ready(val data: ReadyData, override val sequence: Int?) : DispatchEvent()

@Serializable
data class ReadyData(
    @SerialName("v")
    val version: Int,
    val user: DiscordUser,
    @SerialName("private_channels")
    val privateChannels: List<DiscordChannel>,
    val guilds: List<DiscordUnavailableGuild>,
    @SerialName("session_id")
    val sessionId: String,
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
data class Heartbeat(val data: Long) : Event() {
    companion object : KSerializer<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("HeartbeatEvent", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())

        override fun serialize(encoder: Encoder, value: Heartbeat) {
            encoder.encodeLong(value.data)
        }
    }
}

@Serializable
data class Resumed(
    override val sequence: Int?,
) : DispatchEvent()

@Serializable(with = InvalidSession.Serializer::class)
data class InvalidSession(val resumable: Boolean) : Event() {

    internal object Serializer : KSerializer<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.InvalidSession", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean())

        override fun serialize(encoder: Encoder, value: InvalidSession) {
            encoder.encodeBoolean(value.resumable)
        }
    }
}

data class ChannelCreate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()
data class ChannelUpdate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()
data class ChannelDelete(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()
data class ChannelPinsUpdate(val pins: DiscordPinsUpdateData, override val sequence: Int?) : DispatchEvent()

data class TypingStart(val data: DiscordTyping, override val sequence: Int?) : DispatchEvent()
data class GuildCreate(val guild: DiscordGuild, override val sequence: Int?) : DispatchEvent()
data class GuildUpdate(val guild: DiscordGuild, override val sequence: Int?) : DispatchEvent()
data class GuildDelete(val guild: DiscordUnavailableGuild, override val sequence: Int?) : DispatchEvent()
data class GuildBanAdd(val ban: DiscordGuildBan, override val sequence: Int?) : DispatchEvent()
data class GuildBanRemove(val ban: DiscordGuildBan, override val sequence: Int?) : DispatchEvent()
data class GuildEmojisUpdate(val emoji: DiscordUpdatedEmojis, override val sequence: Int?) : DispatchEvent()
data class GuildIntegrationsUpdate(val integrations: DiscordGuildIntegrations, override val sequence: Int?) :
    DispatchEvent()

data class GuildMemberAdd(val member: DiscordAddedGuildMember, override val sequence: Int?) : DispatchEvent()
data class GuildMemberRemove(val member: DiscordRemovedGuildMember, override val sequence: Int?) : DispatchEvent()
data class GuildMemberUpdate(val member: DiscordUpdatedGuildMember, override val sequence: Int?) : DispatchEvent()
data class GuildRoleCreate(val role: DiscordGuildRole, override val sequence: Int?) : DispatchEvent()
data class GuildRoleUpdate(val role: DiscordGuildRole, override val sequence: Int?) : DispatchEvent()
data class GuildRoleDelete(val role: DiscordDeletedGuildRole, override val sequence: Int?) : DispatchEvent()
data class GuildMembersChunk(val data: GuildMembersChunkData, override val sequence: Int?) : DispatchEvent()

/**
 * Sent when a new invite to a channel is created.
 */
data class InviteCreate(val invite: DiscordCreatedInvite, override val sequence: Int?) : DispatchEvent()

/**
 * Sent when an invite is deleted.
 */
data class InviteDelete(val invite: DiscordDeletedInvite, override val sequence: Int?) : DispatchEvent()

@Serializable
data class DiscordDeletedInvite(
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    val code: String,
)

@Serializable
data class DiscordCreatedInvite(
    @SerialName("channel_id")
    val channelId: Snowflake,
    val code: String,
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("guild_id")
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val inviter: Optional<DiscordInviteUser> = Optional.Missing(),
    @SerialName("max_age")
    val maxAge: Int,
    @SerialName("max_uses")
    val maxUses: Int,
    @SerialName("target_user")
    val targetUser: Optional<DiscordInviteUser> = Optional.Missing(),
    @SerialName("target_user_type")
    val targetUserType: Optional<TargetUserType> = Optional.Missing(),
    val temporary: Boolean,
    val uses: Int,
)

@Serializable
data class DiscordInviteUser(
    val id: Snowflake,
    val username: String,
    val discriminator: String,
    val avatar: String?,
    val bot: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("public_flags")
    val publicFlags: Optional<UserFlags> = Optional.Missing(),
)

data class MessageCreate(val message: DiscordMessage, override val sequence: Int?) : DispatchEvent()
data class MessageUpdate(val message: DiscordPartialMessage, override val sequence: Int?) : DispatchEvent()
data class MessageDelete(val message: DeletedMessage, override val sequence: Int?) : DispatchEvent()
data class MessageDeleteBulk(val messageBulk: BulkDeleteData, override val sequence: Int?) : DispatchEvent()
data class MessageReactionAdd(val reaction: MessageReactionAddData, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemove(val reaction: MessageReactionRemoveData, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions, override val sequence: Int?) :
    DispatchEvent()

data class MessageReactionRemoveEmoji(val reaction: DiscordRemovedEmoji, override val sequence: Int?) : DispatchEvent()

@Serializable
data class DiscordRemovedEmoji(
    @SerialName("channel_id")
    val channelId: Snowflake,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("message_id")
    val messageId: Snowflake,
    val emoji: DiscordRemovedReactionEmoji,
)

@Serializable
data class DiscordRemovedReactionEmoji(
    val id: Snowflake?,
    val name: String?,
)

data class PresenceUpdate(val presence: DiscordPresenceUpdate, override val sequence: Int?) : DispatchEvent()
data class UserUpdate(val user: DiscordUser, override val sequence: Int?) : DispatchEvent()
data class VoiceStateUpdate(val voiceState: DiscordVoiceState, override val sequence: Int?) : DispatchEvent()
data class VoiceServerUpdate(val voiceServerUpdateData: DiscordVoiceServerUpdateData, override val sequence: Int?) :
    DispatchEvent()

data class WebhooksUpdate(val webhooksUpdateData: DiscordWebhooksUpdateData, override val sequence: Int?) :
    DispatchEvent()

@KordPreview
data class InteractionCreate(val interaction: DiscordInteraction, override val sequence: Int?) : DispatchEvent()

@KordPreview
data class ApplicationCommandCreate(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()

@KordPreview
data class ApplicationCommandUpdate(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()

@KordPreview
data class ApplicationCommandDelete(val application: DiscordApplicationCommand, override val sequence: Int?) :
    DispatchEvent()

data class ThreadCreate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()

data class ThreadUpdate(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()

data class ThreadDelete(val channel: DiscordChannel, override val sequence: Int?) : DispatchEvent()

data class ThreadMemberUpdate(val member: DiscordThreadMember, override val sequence: Int?) : DispatchEvent()

data class ThreadListSync(val sync: DiscordThreadListSync, override val sequence: Int?) : DispatchEvent()

data class ThreadMembersUpdate(val members: DiscordThreadMembersUpdate, override val sequence: Int?) : DispatchEvent()

@Serializable
data class DiscordThreadListSync(
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_ids")
    val channelIds: Optional<List<Snowflake>> = Optional.Missing(),
    val theads: List<DiscordChannel>,
    val members: List<DiscordThreadMember>
)

@Serializable
data class DiscordThreadMembersUpdate(
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