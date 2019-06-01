package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.BooleanDescriptor
import kotlinx.serialization.internal.LongDescriptor
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@Serializable
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
data class Resumed(
        @SerialName("_traces")
        val traces: List<String>
) : Event()

@Serializable
data class InvalidSession(val resumable: Boolean) : Event() {
    @Serializer(InvalidSession::class)
    companion object : KSerializer<InvalidSession> {
        override val descriptor: SerialDescriptor
            get() = BooleanDescriptor.withName("InvalidSession")

        override fun deserialize(decoder: Decoder) = InvalidSession(decoder.decodeBoolean())
        override fun serialize(encoder: Encoder, obj: InvalidSession) = Unit
    }
}

@Serializable
data class Heartbeat(val data: Long) : Event() {
    @Serializer(Heartbeat::class)
    companion object : KSerializer<Heartbeat> {
        override val descriptor: SerialDescriptor
            get() = LongDescriptor.withName("HeartbeatEvent")

        override fun deserialize(decoder: Decoder) = Heartbeat(decoder.decodeLong())
        override fun serialize(encoder: Encoder, obj: Heartbeat) = Unit

    }
}

@Serializable
data class ChannelCreate(val channel: Channel) : Event()

@Serializable
data class ChannelUpdate(val channel: Channel) : Event()

@Serializable
data class ChannelDelete(val channel: Channel) : Event()

@Serializable
data class ChannelPinsUpdate(val pins: PinsUpdateData) : Event()

@Serializable
data class TypingStart(val data: Typing) : Event()

@Serializable
data class GuildCreate(val guild: Guild) : Event()

@Serializable
data class GuildUpdate(val guild: Guild) : Event()

@Serializable
data class GuildDelete(val guild: UnavailableGuild) : Event()

@Serializable
data class GuildBanAdd(val ban: GuildBan) : Event()

@Serializable
data class GuildBanRemove(val ban: GuildBan) : Event()

@Serializable
data class GuildEmojisUpdate(val emoji: UpdatedEmojis) : Event()

@Serializable
data class GuildIntegrationsUpdate(val integrations: GuildIntegrations) : Event()

@Serializable
data class GuildMemberAdd(val member: AddedGuildMember) : Event()

@Serializable
data class GuildMemberRemove(val member: RemovedGuildMember) : Event()

@Serializable
data class GuildMemberUpdate(val member: UpdatedGuildMember) : Event()

@Serializable
data class GuildRoleCreate(val role: GuildRole) : Event()

@Serializable
data class GuildRoleUpdate(val role: GuildRole) : Event()

@Serializable
data class GuildRoleDelete(val role: DeletedGuildRole) : Event()

@Serializable
data class GuildMembersChunk(val data: GuildMembersChunkData) : Event()

@Serializable
data class MessageCreate(val message: Message) : Event()

@Serializable
data class MessageUpdate(val message: Message) : Event()

@Serializable
data class MessageDelete(val message: DeletedMessage) : Event()

@Serializable
data class MessageDeleteBulk(val messageBulk: BulkDeleteData) : Event()

@Serializable
data class MessageReactionAdd(val reaction: MessageReaction) : Event()

@Serializable
data class MessageReactionRemove(val reaction: MessageReaction) : Event()

@Serializable
data class MessageReactionRemoveAll(val reactions: AllRemovedMessageReactions) : Event()

@Serializable
data class PresenceUpdate(val presence: PresenceUpdateData) : Event()

@Serializable
data class UserUpdate(val user: User) : Event()

@Serializable
data class VoiceStateUpdate(val voiceState: VoiceState) : Event()

@Serializable
data class VoiceServerUpdate(val voiceServerUpdateData: VoiceServerUpdateData) : Event()

@Serializable
data class WebhooksUpdate(val webhooksUpdateData: WebhooksUpdateData) : Event()

@UnstableDefault

sealed class Command

@UnstableDefault
fun <T : Command> JsonObject.command(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

