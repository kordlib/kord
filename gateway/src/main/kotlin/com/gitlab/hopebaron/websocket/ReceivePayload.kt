package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.OpCode.Dispatch
import com.gitlab.hopebaron.websocket.OpCode.Hello
import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl

@Serializable
data class ReceivePayload(
        val event: Event,
        val sequence: Int?) {
    @Serializer(ReceivePayload::class)
    companion object : KSerializer<ReceivePayload> {
        override val descriptor: SerialDescriptor
            get() = object : SerialClassDescImpl("ReceivePayload") {
                init {
                    addElement("op")
                    addElement("t", true)
                    addElement("s", true)
                    addElement("d", true)

                }
            }

        override fun deserialize(decoder: Decoder): ReceivePayload {
            lateinit var op: OpCode
            lateinit var data: Event
            var sequence: Int? = null
            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val index = decodeElementIndex(descriptor)) {
                        CompositeDecoder.READ_DONE -> break@loop
                        0 -> op = OpCode.deserialize(decoder)
                        1 -> data = when (op) {
                            Dispatch -> {
                                val name = decodeStringElement(descriptor, index)
                                sequence = decodeIntElement(descriptor, index)
                                getByDispatchEvent(index, this, name)
                            }
                            OpCode.Heartbeat -> decodeSerializableElement(descriptor, index, Heartbeat.serializer())
                            OpCode.InvalidSession -> decodeSerializableElement(descriptor, index, InvalidSession)
                            Hello -> decodeSerializableElement(descriptor, index, HelloEvent.serializer())
                            OpCode.HeartbeatACK -> HeartbeatACK
                            OpCode.Reconnect -> Reconnect
                            else -> error("This op code doesn't belong to an event.")
                        }
                    }
                }
                endStructure(descriptor)
                return ReceivePayload(data, sequence)
            }
        }


        private fun getByDispatchEvent(index: Int, decoder: CompositeDecoder, name: String?) = when (name) {
            "CHANNEL_CREATE" -> ChannelCreate(decoder.decodeSerializableElement(descriptor, index, Channel.serializer()))
            "CHANNEL_UPDATE" -> ChannelUpdate(decoder.decodeSerializableElement(descriptor, index, Channel.serializer()))
            "CHANNEL_DELETE" -> ChannelDelete(decoder.decodeSerializableElement(descriptor, index, Channel.serializer()))
            "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(decoder.decodeSerializableElement(descriptor, index, PinsUpdateData.serializer()))
            "TYPING_START" -> TypingStart(decoder.decodeSerializableElement(descriptor, index, Typing.serializer()))
            "GUILD_CREATE" -> GuildCreate(decoder.decodeSerializableElement(descriptor, index, Guild.serializer()))
            "GUILD_UPDATE" -> GuildUpdate(decoder.decodeSerializableElement(descriptor, index, Guild.serializer()))
            "GUILD_DELETE" -> GuildDelete(decoder.decodeSerializableElement(descriptor, index, UnavailableGuild.serializer()))
            "GUILD_BAN_ADD" -> GuildBanAdd(decoder.decodeSerializableElement(descriptor, index, GuildBan.serializer()))
            "GUILD_BAN_REMOVE" -> GuildBanRemove(decoder.decodeSerializableElement(descriptor, index, GuildBan.serializer()))
            "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(decoder.decodeSerializableElement(descriptor, index, UpdatedEmojis.serializer()))
            "GUILD_INTEGRATIONS_UPDATE" -> GuildIntegrationsUpdate(decoder.decodeSerializableElement(descriptor, index, GuildIntegrations.serializer()))
            "GUILD_MEMBER_ADD" -> GuildMemberAdd(decoder.decodeSerializableElement(descriptor, index, AddedGuildMember.serializer()))
            "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(decoder.decodeSerializableElement(descriptor, index, RemovedGuildMember.serializer()))
            "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(decoder.decodeSerializableElement(descriptor, index, UpdatedGuildMember.serializer()))
            "GUILD_ROLE_CREATE" -> GuildRoleCreate(decoder.decodeSerializableElement(descriptor, index, GuildRole.serializer()))
            "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(decoder.decodeSerializableElement(descriptor, index, GuildRole.serializer()))
            "GUILD_ROLE_DELETE" -> GuildRoleDelete(decoder.decodeSerializableElement(descriptor, index, DeletedGuildRole.serializer()))
            "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(decoder.decodeSerializableElement(descriptor, index, GuildMembersChunkData.serializer()))

            "MESSAGE_CREATE" -> MessageCreate(decoder.decodeSerializableElement(descriptor, index, Message.serializer()))
            "MESSAGE_UPDATE" -> MessageUpdate(decoder.decodeSerializableElement(descriptor, index, Message.serializer()))
            "MESSAGE_DELETE" -> MessageDelete(decoder.decodeSerializableElement(descriptor, index, DeletedMessage.serializer()))
            "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(decoder.decodeSerializableElement(descriptor, index, BulkDeleteData.serializer()))
            "MESSAGE_REACTION_ADD" -> MessageReactionAdd(decoder.decodeSerializableElement(descriptor, index, MessageReaction.serializer()))
            "MESSAGE_REACTION_REMOVE" -> MessageReactionRemove(decoder.decodeSerializableElement(descriptor, index, MessageReaction.serializer()))

            "MESSAGE_REACTION_REMOVE_ALL" -> MessageReactionRemoveAll(decoder.decodeSerializableElement(descriptor, index, AllRemovedMessageReactions.serializer()))
            "PRESENCE_UPDATE" -> PresenceUpdate(decoder.decodeSerializableElement(descriptor, index, PresenceUpdateData.serializer()))
            "USER_UPDATE" -> UserUpdate(decoder.decodeSerializableElement(descriptor, index, User.serializer()))
            "VOICE_STATE_UPDATE" -> VoiceStateUpdate(decoder.decodeSerializableElement(descriptor, index, VoiceState.serializer()))
            "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(decoder.decodeSerializableElement(descriptor, index, VoiceServerUpdateData.serializer()))
            "WEBHOOKS_UPDATE" -> WebhooksUpdate(decoder.decodeSerializableElement(descriptor, index, WebhooksUpdateData.serializer()))
            else -> TODO("log this event $name")
        }
    }

}


