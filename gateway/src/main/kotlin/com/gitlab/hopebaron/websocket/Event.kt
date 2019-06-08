package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.*

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
        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Event") {
            init {
                addElement("op")
                addElement("t", true)
                addElement("s", true)
                addElement("d", true)

            }
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
                        1 -> eventName = decodeNullableSerializableElement(descriptor, index, NullableSerializer(String.serializer()))
                        2 -> sequence = decodeNullableSerializableElement(descriptor, index, NullableSerializer(Int.serializer()))
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
        val user: User,
        @SerialName("private_channels")
        val privateChannels: List<Channel>, //TODO("Add DM Channel.")
        val guilds: List<UnavailableGuild>,
        @SerialName("session_id")
        val sessionId: String,
        @SerialName("_trace")
        val traces: List<String>,
        val shard: Shard?)

@Serializable
data class Heartbeat(val data: Long) : Event() {
    @Serializer(Heartbeat::class)
    companion object : DeserializationStrategy<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = LongDescriptor.withName("HeartbeatEvent")

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
    }
}

@Serializable
data class Resumed(val data: ResumedData, override val sequence: Int?) : DispatchEvent()

@Serializable
data class ResumedData(
        @SerialName("_traces")
        val traces: List<String>
) {
    @Serializer(Heartbeat::class)
    companion object : DeserializationStrategy<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = LongDescriptor.withName("HeartbeatEvent")

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
    }
}


@Serializable
data class InvalidSession(val resumable: Boolean) : Event() {
    @Serializer(InvalidSession::class)
    companion object : DeserializationStrategy<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = BooleanDescriptor.withName("InvalidSession")

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean())
    }
}


data class ChannelCreate(val channel: Channel, override val sequence: Int?) : DispatchEvent()
data class ChannelUpdate(val channel: Channel, override val sequence: Int?) : DispatchEvent()
data class ChannelDelete(val channel: Channel, override val sequence: Int?) : DispatchEvent()
data class ChannelPinsUpdate(val pins: PinsUpdateData, override val sequence: Int?) : DispatchEvent()

data class TypingStart(val data: Typing, override val sequence: Int?) : DispatchEvent()
data class GuildCreate(val guild: Guild, override val sequence: Int?) : DispatchEvent()
data class GuildUpdate(val guild: Guild, override val sequence: Int?) : DispatchEvent()
data class GuildDelete(val guild: UnavailableGuild, override val sequence: Int?) : DispatchEvent()
data class GuildBanAdd(val ban: GuildBan, override val sequence: Int?) : DispatchEvent()
data class GuildBanRemove(val ban: GuildBan, override val sequence: Int?) : DispatchEvent()
data class GuildEmojisUpdate(val emoji: UpdatedEmojis, override val sequence: Int?) : DispatchEvent()
data class GuildIntegrationsUpdate(val integrations: GuildIntegrations, override val sequence: Int?) : DispatchEvent()
data class GuildMemberAdd(val member: AddedGuildMember, override val sequence: Int?) : DispatchEvent()
data class GuildMemberRemove(val member: RemovedGuildMember, override val sequence: Int?) : DispatchEvent()
data class GuildMemberUpdate(val member: UpdatedGuildMember, override val sequence: Int?) : DispatchEvent()
data class GuildRoleCreate(val role: GuildRole, override val sequence: Int?) : DispatchEvent()
data class GuildRoleUpdate(val role: GuildRole, override val sequence: Int?) : DispatchEvent()
data class GuildRoleDelete(val role: DeletedGuildRole, override val sequence: Int?) : DispatchEvent()
data class GuildMembersChunk(val data: GuildMembersChunkData, override val sequence: Int?) : DispatchEvent()

data class MessageCreate(val message: Message, override val sequence: Int?) : DispatchEvent()
data class MessageUpdate(val message: Message, override val sequence: Int?) : DispatchEvent()
data class MessageDelete(val message: DeletedMessage, override val sequence: Int?) : DispatchEvent()
data class MessageDeleteBulk(val messageBulk: BulkDeleteData, override val sequence: Int?) : DispatchEvent()
data class MessageReactionAdd(val reaction: MessageReaction, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemove(val reaction: MessageReaction, override val sequence: Int?) : DispatchEvent()
data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions, override val sequence: Int?) : DispatchEvent()

data class PresenceUpdate(val presence: PresenceUpdateData, override val sequence: Int?) : DispatchEvent()
data class UserUpdate(val user: User, override val sequence: Int?) : DispatchEvent()
data class VoiceStateUpdate(val voiceState: VoiceState, override val sequence: Int?) : DispatchEvent()
data class VoiceServerUpdate(val voiceServerUpdateData: VoiceServerUpdateData, override val sequence: Int?) : DispatchEvent()
data class WebhooksUpdate(val webhooksUpdateData: WebhooksUpdateData, override val sequence: Int?) : DispatchEvent()
