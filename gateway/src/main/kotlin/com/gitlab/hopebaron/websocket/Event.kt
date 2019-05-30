package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
sealed class Event

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

@UnstableDefault
fun <T : Event> JsonObject.event(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

private fun <T> fromJson(serializer: KSerializer<T>, element: JsonElement?) = Json.plain.fromJson(serializer, element!!)

@UnstableDefault
internal fun Payload.event(): Event = when (name) {
    "CHANNEL_CREATE" -> ChannelCreate(fromJson(Channel.serializer(), data))
    "CHANNEL_UPDATE" -> ChannelUpdate(fromJson(Channel.serializer(), data))
    "CHANNEL_DELETE" -> ChannelDelete(fromJson(Channel.serializer(), data))
    "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(fromJson(PinsUpdateData.serializer(), data))

    "TYPING_START" -> TypingStart(fromJson(Typing.serializer(), data))

    "GUILD_CREATE" -> GuildCreate(fromJson(Guild.serializer(), data))
    "GUILD_UPDATE" -> GuildUpdate(fromJson(Guild.serializer(), data))
    "GUILD_DELETE" -> GuildDelete(fromJson(UnavailableGuild.serializer(), data))
    "GUILD_BAN_ADD" -> GuildBanAdd(fromJson(GuildBan.serializer(), data))
    "GUILD_BAN_REMOVE" -> GuildBanRemove(fromJson(GuildBan.serializer(), data))
    "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(fromJson(UpdatedEmojis.serializer(), data))
    "GUILD_INTEGRATIONS_UPDATE" -> GuildIntegrationsUpdate(fromJson(GuildIntegrations.serializer(), data))
    "GUILD_MEMBER_ADD" -> GuildMemberAdd(fromJson(AddedGuildMember.serializer(), data))
    "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(fromJson(RemovedGuildMember.serializer(), data))
    "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(fromJson(UpdatedGuildMember.serializer(), data))
    "GUILD_ROLE_CREATE" -> GuildRoleCreate(fromJson(GuildRole.serializer(), data))
    "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(fromJson(GuildRole.serializer(), data))
    "GUILD_ROLE_DELETE" -> GuildRoleDelete(fromJson(DeletedGuildRole.serializer(), data))
    "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(fromJson(GuildMembersChunkData.serializer(), data))

    "MESSAGE_CREATE" -> MessageCreate(fromJson(Message.serializer(), data))
    "MESSAGE_UPDATE" -> MessageUpdate(fromJson(Message.serializer(), data))
    "MESSAGE_DELETE" -> MessageDelete(fromJson(DeletedMessage.serializer(), data))
    "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(fromJson(BulkDeleteData.serializer(), data))
    "MESSAGE_REACTION_ADD" -> MessageReactionAdd(fromJson(MessageReaction.serializer(), data))
    "MESSAGE_REACTION_REMOVE" -> MessageReactionRemove(fromJson(MessageReaction.serializer(), data))

    "MESSAGE_REACTION_REMOVE_ALL" -> MessageReactionRemoveAll(fromJson(AllRemovedMessageReactions.serializer(), data))
    "PRESENCE_UPDATE" -> PresenceUpdate(fromJson(PresenceUpdateData.serializer(), data))
    "USER_UPDATE" -> UserUpdate(fromJson(User.serializer(), data))
    "VOICE_STATE_UPDATE" -> VoiceStateUpdate(fromJson(VoiceState.serializer(), data))
    "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(fromJson(VoiceServerUpdateData.serializer(), data))
    "WEBHOOKS_UPDATE" -> WebhooksUpdate(fromJson(WebhooksUpdateData.serializer(), data))
    else -> TODO("log this event $name")
}

sealed class Command

@UnstableDefault
fun <T : Command> JsonObject.command(serializer: KSerializer<T>) = Json.plain.fromJson(serializer, this)

