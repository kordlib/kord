package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.internal.*
import kotlinx.serialization.json.JsonElementSerializer
import kotlinx.serialization.json.JsonObject
import mu.KotlinLogging

private val jsonLogger = KotlinLogging.logger { }

sealed class DispatchEvent : Event() {
    abstract val sequence: Int?
}

private object NullDecoder : DeserializationStrategy<Nothing?> {
    override val descriptor: SerialDescriptor
        get() = StringDescriptor

    override fun deserialize(decoder: Decoder): Nothing? {
        return decoder.decodeNull()
    }

    override fun patch(decoder: Decoder, old: Nothing?): Nothing? = throw NotImplementedError()
}

sealed class Event {
    companion object : DeserializationStrategy<Event?> {
        override val descriptor: SerialDescriptor = SerialDescriptor("Event") {
                element("op", OpCode.descriptor)
                element("t", String.serializer().descriptor, isOptional = true)
                element("s", Int.serializer().descriptor, isOptional =  true)
                element("d", JsonObject.serializer().descriptor,  isOptional = true)
            }

        override fun deserialize(decoder: Decoder): Event? {
            var op: OpCode? = null
            var data: Event? = null
            var sequence: Int? = null
            var eventName: String? = null
            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.READ_DONE -> break@loop
                        0 -> {
                            op = OpCode.deserialize(decoder)
                            @Suppress("NON_EXHAUSTIVE_WHEN")
                            when (op) {
                                OpCode.HeartbeatACK -> data = HeartbeatACK
                                OpCode.Reconnect -> data = Reconnect
                            }
                        }
                        1 -> eventName = decodeNullableSerializableElement(descriptor, index, String.serializer().nullable)
                        2 -> sequence = decodeNullableSerializableElement(descriptor, index, Int.serializer().nullable)
                        3 -> data = when (op) {
                            OpCode.Dispatch -> getByDispatchEvent(index, this, eventName, sequence)
                            OpCode.Heartbeat -> decodeSerializableElement(descriptor, index, Heartbeat.serializer())
                            OpCode.HeartbeatACK -> {
                                this.decodeSerializableElement(descriptor, index, NullDecoder)
                                HeartbeatACK
                            }
                            OpCode.InvalidSession -> decodeSerializableElement(descriptor, index, InvalidSession)
                            OpCode.Hello -> decodeSerializableElement(descriptor, index, Hello.serializer())
                            else -> error("This op code ${op?.code} doesn't belong to an event.")
                        }
                    }
                }
                endStructure(descriptor)
                return data
            }
        }


        private fun getByDispatchEvent(index: Int, decoder: CompositeDecoder, name: String?, sequence: Int?) = when (name) {
            "RESUMED" -> Resumed(decoder.decodeSerializableElement(descriptor, index, ResumedData.serializer()), sequence)
            "READY" -> Ready(decoder.decodeSerializableElement(descriptor, index, ReadyData.serializer()), sequence)
            "CHANNEL_CREATE" -> ChannelCreate(decoder.decodeSerializableElement(descriptor, index, DiscordChannel.serializer()), sequence)
            "CHANNEL_UPDATE" -> ChannelUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordChannel.serializer()), sequence)
            "CHANNEL_DELETE" -> ChannelDelete(decoder.decodeSerializableElement(descriptor, index, DiscordChannel.serializer()), sequence)
            "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordPinsUpdateData.serializer()), sequence)
            "TYPING_START" -> TypingStart(decoder.decodeSerializableElement(descriptor, index, DiscordTyping.serializer()), sequence)
            "GUILD_CREATE" -> GuildCreate(decoder.decodeSerializableElement(descriptor, index, DiscordGuild.serializer()), sequence)
            "GUILD_UPDATE" -> GuildUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordGuild.serializer()), sequence)
            "GUILD_DELETE" -> GuildDelete(decoder.decodeSerializableElement(descriptor, index, DiscordUnavailableGuild.serializer()), sequence)
            "GUILD_BAN_ADD" -> GuildBanAdd(decoder.decodeSerializableElement(descriptor, index, DiscordGuildBan.serializer()), sequence)
            "GUILD_BAN_REMOVE" -> GuildBanRemove(decoder.decodeSerializableElement(descriptor, index, DiscordGuildBan.serializer()), sequence)
            "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordUpdatedEmojis.serializer()), sequence)
            "GUILD_INTEGRATIONS_UPDATE" -> GuildIntegrationsUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordGuildIntegrations.serializer()), sequence)
            "GUILD_MEMBER_ADD" -> GuildMemberAdd(decoder.decodeSerializableElement(descriptor, index, DiscordAddedGuildMember.serializer()), sequence)
            "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(decoder.decodeSerializableElement(descriptor, index, DiscordRemovedGuildMember.serializer()), sequence)
            "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordUpdatedGuildMember.serializer()), sequence)
            "GUILD_ROLE_CREATE" -> GuildRoleCreate(decoder.decodeSerializableElement(descriptor, index, DiscordGuildRole.serializer()), sequence)
            "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordGuildRole.serializer()), sequence)
            "GUILD_ROLE_DELETE" -> GuildRoleDelete(decoder.decodeSerializableElement(descriptor, index, DiscordDeletedGuildRole.serializer()), sequence)
            "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(decoder.decodeSerializableElement(descriptor, index, GuildMembersChunkData.serializer()), sequence)

            "INVITE_CREATE" -> InviteCreate(decoder.decodeSerializableElement(descriptor, index, DiscordCreatedInvite.serializer()), sequence)
            "INVITE_DELETE" -> InviteDelete(decoder.decodeSerializableElement(descriptor, index, DiscordDeletedInvite.serializer()), sequence)

            "MESSAGE_CREATE" -> MessageCreate(decoder.decodeSerializableElement(descriptor, index, DiscordMessage.serializer()), sequence)
            "MESSAGE_UPDATE" -> MessageUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordPartialMessage.serializer()), sequence)
            "MESSAGE_DELETE" -> MessageDelete(decoder.decodeSerializableElement(descriptor, index, DeletedMessage.serializer()), sequence)
            "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(decoder.decodeSerializableElement(descriptor, index, BulkDeleteData.serializer()), sequence)
            "MESSAGE_REACTION_ADD" -> MessageReactionAdd(decoder.decodeSerializableElement(descriptor, index, MessageReaction.serializer()), sequence)
            "MESSAGE_REACTION_REMOVE" -> MessageReactionRemove(decoder.decodeSerializableElement(descriptor, index, MessageReaction.serializer()), sequence)
            "MESSAGE_REACTION_REMOVE_EMOJI" -> MessageReactionRemoveEmoji(decoder.decodeSerializableElement(descriptor, index, DiscordRemovedEmoji.serializer()), sequence)

            "MESSAGE_REACTION_REMOVE_ALL" -> MessageReactionRemoveAll(decoder.decodeSerializableElement(descriptor, index, AllRemovedMessageReactions.serializer()), sequence)
            "PRESENCE_UPDATE" -> PresenceUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordPresenceUpdateData.serializer()), sequence)
            "USER_UPDATE" -> UserUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordUser.serializer()), sequence)
            "VOICE_STATE_UPDATE" -> VoiceStateUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordVoiceState.serializer()), sequence)
            "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordVoiceServerUpdateData.serializer()), sequence)
            "WEBHOOKS_UPDATE" -> WebhooksUpdate(decoder.decodeSerializableElement(descriptor, index, DiscordWebhooksUpdateData.serializer()), sequence)
            else -> {
                jsonLogger.warn { "unknown gateway event name $name" }
                // consume json elements that are unknown to us
                decoder.decodeSerializableElement(descriptor, index, JsonElementSerializer.nullable)
                null
            }
        }

        override fun patch(decoder: Decoder, old: Event?): Event? = error("")
    }

}

sealed class Close : Event()
object SessionClose : Close()
object CloseForReconnect : Close()

object HeartbeatACK : Event()
object Reconnect : Event()

@Serializable
data class Hello(
        @SerialName("heartbeat_interval")
        val heartbeatInterval: Long,
        @SerialName("_trace")
        val traces: List<String>
) : Event()

data class Ready(val data: ReadyData, override val sequence: Int?) : DispatchEvent()

@Serializable
data class ReadyData(
        @SerialName("v")
        val version: Int,
        val user: DiscordUser,
        @SerialName("private_channels")
        val privateChannels: List<DiscordChannel>, //TODO("Add DM Channel.")
        val guilds: List<DiscordUnavailableGuild>,
        @SerialName("session_id")
        val sessionId: String,
        @SerialName("_trace")
        val traces: List<String>,
        val shard: DiscordShard?)

@Serializable
data class Heartbeat(val data: Long) : Event() {
    @Serializer(Heartbeat::class)
    companion object : DeserializationStrategy<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("HeartbeatEvent", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
    }
}

@Serializable
data class Resumed(val data: ResumedData, override val sequence: Int?) : DispatchEvent()

@Serializable
data class ResumedData(
        @SerialName("_trace")
        val traces: List<String>
) {
    @Serializer(Heartbeat::class)
    companion object : DeserializationStrategy<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("HeartbeatEvent", PrimitiveKind.LONG)

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
    }
}


@Serializable
data class InvalidSession(val resumable: Boolean) : Event() {
    @Serializer(InvalidSession::class)
    companion object : DeserializationStrategy<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor("InvalidSession", PrimitiveKind.BOOLEAN)

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean())
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
data class GuildIntegrationsUpdate(val integrations: DiscordGuildIntegrations, override val sequence: Int?) : DispatchEvent()
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
        /**
         * The channel of the invite.
         */
        @SerialName("channel_id")
        val channelId: String,
        /**
         * The guild of the invite.
         */
        @SerialName("guild_id")
        val guildId: String,
        /**
         * The unique invite code.
         */
        val code: String
)

@Serializable
data class DiscordCreatedInvite(
        /**
         * The channel the invite is for.
         */
        @SerialName("channel_id")
        val channelId: String,
        /**
         * The unique invite code.
         */
        val code: String,
        /**
         * The time at which the invite was created.
         */
        @SerialName("created_at")
        val createdAt: String,
        /**
         * The guild of the invite.
         */
        @SerialName("guild_id")
        val guildId: String,
        /**
         * The user that created the invite.
         */
        val inviter: DiscordInviteUser,
        /**
         * How long the invite is valid for (in seconds).
         */
        @SerialName("max_age")
        val maxAge: Int,
        /**
         * The maximum number of times the invite can be used.
         */
        @SerialName("ma_uses")
        val maxUses: Int,
        /**
         * Whether or not the invite is temporary (invited users will be kicked on disconnect unless they're assigned a role).
         */
        val temporary: Boolean,
        /**
         * How many times the invite has been used (always will be 0).
         */
        val uses: Int
)

@Serializable
data class DiscordInviteUser(
        val avatar: String,
        val discriminator: String,
        val id: String,
        val username: String
)

data class MessageCreate(val message: DiscordMessage, override val sequence: Int?) : DispatchEvent()
data class MessageUpdate(val message: DiscordPartialMessage, override val sequence: Int?) : DispatchEvent()
data class MessageDelete(val message: DeletedMessage, override val sequence: Int?) : DispatchEvent()
data class MessageDeleteBulk(val messageBulk: BulkDeleteData, override val sequence: Int?) : DispatchEvent()
data class MessageReactionAdd(val reaction: MessageReaction, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemove(val reaction: MessageReaction, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemoveEmoji(val reaction: DiscordRemovedEmoji, override val sequence: Int?) : DispatchEvent()

@Serializable
data class DiscordRemovedEmoji(
        /**
         * The id of the channel.
         */
        @SerialName("channel_id")
        val channelId: String,

        /**
         * The id of the guild.
         */
        @SerialName("guild_id")
        val guildId: String,

        /**
         * The id of the message.
         */
        @SerialName("message_id")
        val messageId: String,

        /**
         * The emoji that was removed.
         */
        val emoji: DiscordRemovedReactionEmoji
)

@Serializable
data class DiscordRemovedReactionEmoji(
        /**
         * The id of the emoji.
         */
        val id: String?,
        /**
         * The name of the emoji.
         */
        val name: String
)

data class PresenceUpdate(val presence: DiscordPresenceUpdateData, override val sequence: Int?) : DispatchEvent()
data class UserUpdate(val user: DiscordUser, override val sequence: Int?) : DispatchEvent()
data class VoiceStateUpdate(val voiceState: DiscordVoiceState, override val sequence: Int?) : DispatchEvent()
data class VoiceServerUpdate(val voiceServerUpdateData: DiscordVoiceServerUpdateData, override val sequence: Int?) : DispatchEvent()
data class WebhooksUpdate(val webhooksUpdateData: DiscordWebhooksUpdateData, override val sequence: Int?) : DispatchEvent()
