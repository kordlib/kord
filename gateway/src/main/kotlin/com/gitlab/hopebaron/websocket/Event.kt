package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.BooleanDescriptor
import kotlinx.serialization.internal.LongDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl


@Serializable
sealed class Event {
    abstract val sequence: Int?

    @Serializer(Event::class)
    companion object : DeserializationStrategy<Event> {
        override val descriptor: SerialDescriptor
            get() = object : SerialClassDescImpl("Payload") {
                init {
                    addElement("op")
                    addElement("t", true)
                    addElement("s", true)
                    addElement("d", true)

                }
            }

        override fun deserialize(decoder: Decoder): Event {
            lateinit var op: OpCode
            lateinit var data: Event
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
                        1 -> eventName = decodeStringElement(descriptor, index)
                        2 -> sequence = decodeIntElement(descriptor, index)
                        3 -> data = when (op) {
                            OpCode.Dispatch -> getByDispatchEvent(index, this, eventName, sequence)
                            OpCode.Heartbeat -> decodeSerializableElement(descriptor, index, Heartbeat.serializer())
                            OpCode.InvalidSession -> decodeSerializableElement(descriptor, index, InvalidSession)
                            OpCode.Hello -> decodeSerializableElement(descriptor, index, Hello.serializer())
                            else -> error("This op code doesn't belong to an event.")
                        }
                    }
                }
                endStructure(descriptor)
                return data
            }
        }


        private fun getByDispatchEvent(index: Int, decoder: CompositeDecoder, name: String?, sequence: Int?) = when (name) {
            "RESUMED" -> decoder.decodeSerializableElement(descriptor, index, Resumed.serializer())
            "READY" -> decoder.decodeSerializableElement(descriptor, index, Ready.serializer())
            "CHANNEL_CREATE" -> ChannelCreate(decoder.decodeSerializableElement(descriptor, index, Channel.serializer()), sequence)
            "CHANNEL_UPDATE" -> ChannelUpdate(decoder.decodeSerializableElement(descriptor, index, Channel.serializer()), sequence)
            "CHANNEL_DELETE" -> ChannelDelete(decoder.decodeSerializableElement(descriptor, index, Channel.serializer()), sequence)
            "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(decoder.decodeSerializableElement(descriptor, index, PinsUpdateData.serializer()), sequence)
            "TYPING_START" -> TypingStart(decoder.decodeSerializableElement(descriptor, index, Typing.serializer()), sequence)
            "GUILD_CREATE" -> GuildCreate(decoder.decodeSerializableElement(descriptor, index, Guild.serializer()), sequence)
            "GUILD_UPDATE" -> GuildUpdate(decoder.decodeSerializableElement(descriptor, index, Guild.serializer()), sequence)
            "GUILD_DELETE" -> GuildDelete(decoder.decodeSerializableElement(descriptor, index, UnavailableGuild.serializer()), sequence)
            "GUILD_BAN_ADD" -> GuildBanAdd(decoder.decodeSerializableElement(descriptor, index, GuildBan.serializer()), sequence)
            "GUILD_BAN_REMOVE" -> GuildBanRemove(decoder.decodeSerializableElement(descriptor, index, GuildBan.serializer()), sequence)
            "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(decoder.decodeSerializableElement(descriptor, index, UpdatedEmojis.serializer()), sequence)
            "GUILD_INTEGRATIONS_UPDATE" -> GuildIntegrationsUpdate(decoder.decodeSerializableElement(descriptor, index, GuildIntegrations.serializer()), sequence)
            "GUILD_MEMBER_ADD" -> GuildMemberAdd(decoder.decodeSerializableElement(descriptor, index, AddedGuildMember.serializer()), sequence)
            "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(decoder.decodeSerializableElement(descriptor, index, RemovedGuildMember.serializer()), sequence)
            "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(decoder.decodeSerializableElement(descriptor, index, UpdatedGuildMember.serializer()), sequence)
            "GUILD_ROLE_CREATE" -> GuildRoleCreate(decoder.decodeSerializableElement(descriptor, index, GuildRole.serializer()), sequence)
            "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(decoder.decodeSerializableElement(descriptor, index, GuildRole.serializer()), sequence)
            "GUILD_ROLE_DELETE" -> GuildRoleDelete(decoder.decodeSerializableElement(descriptor, index, DeletedGuildRole.serializer()), sequence)
            "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(decoder.decodeSerializableElement(descriptor, index, GuildMembersChunkData.serializer()), sequence)

            "MESSAGE_CREATE" -> MessageCreate(decoder.decodeSerializableElement(descriptor, index, Message.serializer()), sequence)
            "MESSAGE_UPDATE" -> MessageUpdate(decoder.decodeSerializableElement(descriptor, index, Message.serializer()), sequence)
            "MESSAGE_DELETE" -> MessageDelete(decoder.decodeSerializableElement(descriptor, index, DeletedMessage.serializer()), sequence)
            "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(decoder.decodeSerializableElement(descriptor, index, BulkDeleteData.serializer()), sequence)
            "MESSAGE_REACTION_ADD" -> MessageReactionAdd(decoder.decodeSerializableElement(descriptor, index, MessageReaction.serializer()), sequence)
            "MESSAGE_REACTION_REMOVE" -> MessageReactionRemove(decoder.decodeSerializableElement(descriptor, index, MessageReaction.serializer()), sequence)

            "MESSAGE_REACTION_REMOVE_ALL" -> MessageReactionRemoveAll(decoder.decodeSerializableElement(descriptor, index, AllRemovedMessageReactions.serializer()), sequence)
            "PRESENCE_UPDATE" -> PresenceUpdate(decoder.decodeSerializableElement(descriptor, index, PresenceUpdateData.serializer()), sequence)
            "USER_UPDATE" -> UserUpdate(decoder.decodeSerializableElement(descriptor, index, User.serializer()), sequence)
            "VOICE_STATE_UPDATE" -> VoiceStateUpdate(decoder.decodeSerializableElement(descriptor, index, VoiceState.serializer()), sequence)
            "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(decoder.decodeSerializableElement(descriptor, index, VoiceServerUpdateData.serializer()), sequence)
            "WEBHOOKS_UPDATE" -> WebhooksUpdate(decoder.decodeSerializableElement(descriptor, index, WebhooksUpdateData.serializer()), sequence)
            else -> TODO("log this event $name")
        }
    }

}

object HeartbeatACK : Event() {
    override val sequence: Int?
        get() = null
}

object Reconnect : Event() {
    override val sequence: Int?
        get() = null
}

@Serializable
data class Hello(
        @SerialName("heartbeat_interval")
        val heartbeatInterval: Long,
        @SerialName("_trace")
        val traces: List<String>,
        override val sequence: Int?
) : Event()

@Serializable
data class Ready(
        @SerialName("v")
        val version: Int,
        val user: User,
        @SerialName("private_channels")
        val privateChannels: List<Channel>, //TODO("Add DM Channel.")
        val guilds: List<UnavailableGuild>,
        @SerialName("session_id")
        val sessionId: String,
        @SerialName("_trace")
        val traces: List<String>,
        val shard: Shard?,
        override val sequence: Int?
) : Event()

@Serializable
data class Heartbeat(val data: Long, override val sequence: Int?) : Event() {
    @Serializer(Heartbeat::class)
    companion object : DeserializationStrategy<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = LongDescriptor.withName("HeartbeatEvent")

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong(), null)
    }
}

@Serializable
data class Resumed(
        @SerialName("_traces")
        val traces: List<String>, override val sequence: Int?
) : Event()

@Serializable
data class InvalidSession(val resumable: Boolean, override val sequence: Int?) : Event() {
    @Serializer(InvalidSession::class)
    companion object : DeserializationStrategy<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = BooleanDescriptor.withName("InvalidSession")

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean(), null)
    }
}


data class ChannelCreate(val channel: Channel, override val sequence: Int?) : Event()
data class ChannelUpdate(val channel: Channel, override val sequence: Int?) : Event()
data class ChannelDelete(val channel: Channel, override val sequence: Int?) : Event()
data class ChannelPinsUpdate(val pins: PinsUpdateData, override val sequence: Int?) : Event()

data class TypingStart(val data: Typing, override val sequence: Int?) : Event()
data class GuildCreate(val guild: Guild, override val sequence: Int?) : Event()
data class GuildUpdate(val guild: Guild, override val sequence: Int?) : Event()
data class GuildDelete(val guild: UnavailableGuild, override val sequence: Int?) : Event()
data class GuildBanAdd(val ban: GuildBan, override val sequence: Int?) : Event()
data class GuildBanRemove(val ban: GuildBan, override val sequence: Int?) : Event()
data class GuildEmojisUpdate(val emoji: UpdatedEmojis, override val sequence: Int?) : Event()
data class GuildIntegrationsUpdate(val integrations: GuildIntegrations, override val sequence: Int?) : Event()
data class GuildMemberAdd(val member: AddedGuildMember, override val sequence: Int?) : Event()
data class GuildMemberRemove(val member: RemovedGuildMember, override val sequence: Int?) : Event()
data class GuildMemberUpdate(val member: UpdatedGuildMember, override val sequence: Int?) : Event()
data class GuildRoleCreate(val role: GuildRole, override val sequence: Int?) : Event()
data class GuildRoleUpdate(val role: GuildRole, override val sequence: Int?) : Event()
data class GuildRoleDelete(val role: DeletedGuildRole, override val sequence: Int?) : Event()
data class GuildMembersChunk(val data: GuildMembersChunkData, override val sequence: Int?) : Event()

data class MessageCreate(val message: Message, override val sequence: Int?) : Event()
data class MessageUpdate(val message: Message, override val sequence: Int?) : Event()
data class MessageDelete(val message: DeletedMessage, override val sequence: Int?) : Event()
data class MessageDeleteBulk(val messageBulk: BulkDeleteData, override val sequence: Int?) : Event()
data class MessageReactionAdd(val reaction: MessageReaction, override val sequence: Int?) : Event()
data class MessageReactionRemove(val reaction: MessageReaction, override val sequence: Int?) : Event()
data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions, override val sequence: Int?) : Event()

data class PresenceUpdate(val presence: PresenceUpdateData, override val sequence: Int?) : Event()
data class UserUpdate(val user: User, override val sequence: Int?) : Event()
data class VoiceStateUpdate(val voiceState: VoiceState, override val sequence: Int?) : Event()
data class VoiceServerUpdate(val voiceServerUpdateData: VoiceServerUpdateData, override val sequence: Int?) : Event()
data class WebhooksUpdate(val webhooksUpdateData: WebhooksUpdateData, override val sequence: Int?) : Event()
