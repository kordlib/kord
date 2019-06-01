package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.BooleanDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject


@Polymorphic
sealed class Event

object HeartbeatACK : Event()
object Reconnect : Event()
@Serializable
data class HelloEvent(
        @SerialName("heartbeat_interval")
        val heartbeatInterval: Long,
        @SerialName("_trace")
        val traces: List<String>
) : Event()

@Serializable
data class Heartbeat(val data: Long) {
    @Serializer(Heartbeat::class)
    companion object : KSerializer<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = LongDescriptor.withName("HeartbeatEvent")

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
        override fun serialize(encoder: Encoder, obj: Heartbeat) = error("Events are not supposed to be serialized.")

    }
}


data class Resumed(
        @SerialName("_traces")
        val traces: List<String>
) : Event()


data class InvalidSession(val resumable: Boolean) : Event() {
    @Serializer(InvalidSession::class)
    companion object : KSerializer<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = BooleanDescriptor.withName("InvalidSession")

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean())
        override fun serialize(encoder: Encoder, obj: InvalidSession) = error("Events supposed to be serializable.")
    }
}



data class ChannelCreate(val channel: Channel) : Event()
data class ChannelUpdate(val channel: Channel) : Event()
data class ChannelDelete(val channel: Channel) : Event()
data class ChannelPinsUpdate(val pins: PinsUpdateData) : Event()

data class TypingStart(val data: Typing) : Event()
data class GuildCreate(val guild: Guild) : Event()
data class GuildUpdate(val guild: Guild) : Event()
data class GuildDelete(val guild: UnavailableGuild) : Event()
data class GuildBanAdd(val ban: GuildBan) : Event()
data class GuildBanRemove(val ban: GuildBan) : Event()
data class GuildEmojisUpdate(val emoji: UpdatedEmojis) : Event()
data class GuildIntegrationsUpdate(val integrations: GuildIntegrations) : Event()
data class GuildMemberAdd(val member: AddedGuildMember) : Event()
data class GuildMemberRemove(val member: RemovedGuildMember) : Event()
data class GuildMemberUpdate(val member: UpdatedGuildMember) : Event()
data class GuildRoleCreate(val role: GuildRole) : Event()
data class GuildRoleUpdate(val role: GuildRole) : Event()
data class GuildRoleDelete(val role: DeletedGuildRole) : Event()
data class GuildMembersChunk(val data: GuildMembersChunkData) : Event()

data class MessageCreate(val message: Message) : Event()
data class MessageUpdate(val message: Message) : Event()
data class MessageDelete(val message: DeletedMessage) : Event()
data class MessageDeleteBulk(val messageBulk: BulkDeleteData) : Event()
data class MessageReactionAdd(val reaction: MessageReaction) : Event()
data class MessageReactionRemove(val reaction: MessageReaction) : Event()
data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions) : Event()

data class PresenceUpdate(val presence: PresenceUpdateData) : Event()
data class UserUpdate(val user: User) : Event()
data class VoiceStateUpdate(val voiceState: VoiceState) : Event()
data class VoiceServerUpdate(val voiceServerUpdateData: VoiceServerUpdateData) : Event()
data class WebhooksUpdate(val webhooksUpdateData: WebhooksUpdateData) : Event()

sealed class Command
