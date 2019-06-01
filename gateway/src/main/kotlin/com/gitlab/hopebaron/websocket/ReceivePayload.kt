package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.*
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl

@Serializable
data class ReceivePayload(
        val opCode: OpCode,
        val data: Event? = null,
        val sequence: Int? = null,
        val name: String? = null
) {
    @Serializer(Event::class)
    internal companion object : KSerializer<ReceivePayload> {
        override val descriptor: SerialDescriptor
            get() = object : SerialClassDescImpl("Payload") {
                init {
                    addElement("op")
                    addElement("d", true)
                    addElement("s", true)
                    addElement("t", true)
                }
            }

        override fun deserialize(decoder: Decoder): ReceivePayload {
            lateinit var op: OpCode
            var data: Event? = null
            var sequence: Int? = null
            var name: String? = null
            with(decoder.beginStructure(descriptor)) {
                loop@ while (true) {
                    when (val i = decodeElementIndex(descriptor)) {
                        CompositeDecoder.READ_ALL -> break@loop
                        0 -> op = OpCode.values().first { it.code == decodeIntElement(descriptor, i) }
                        1 -> when (op) {
                            OpCode.InvalidSession -> {
                            }
                            OpCode.Hello -> {
                                data = decodeSerializableElement(descriptor, i, HelloEvent.serializer())
                                break@loop
                            }
                            OpCode.HeartbeatACK -> {
                                data = HeartbeatACK
                                break@loop
                            }
                            OpCode.Reconnect -> {
                                data = Reconnect
                                break@loop
                            }
                            OpCode.Dispatch -> name = decodeStringElement(descriptor, i)
                        }
                        2 -> sequence = decodeIntElement(descriptor, i)
                        3 -> data = getByDispatchEvent(i, this, name)
                    }
                }
                endStructure(descriptor)
            }


            return ReceivePayload(op, data, sequence, name)
        }


        private fun getByDispatchEvent(i: Int, decoder: CompositeDecoder, name: String?) = when (name) {
            "CHANNEL_CREATE" -> ChannelCreate(decoder.decodeSerializableElement(descriptor, i, Channel.serializer()))
            "CHANNEL_UPDATE" -> ChannelUpdate(decoder.decodeSerializableElement(descriptor, i, Channel.serializer()))
            "CHANNEL_DELETE" -> ChannelDelete(decoder.decodeSerializableElement(descriptor, i, Channel.serializer()))
            "CHANNEL_PINS_UPDATE" -> ChannelPinsUpdate(decoder.decodeSerializableElement(descriptor, i, PinsUpdateData.serializer()))
            "TYPING_START" -> TypingStart(decoder.decodeSerializableElement(descriptor, i, Typing.serializer()))
            "GUILD_CREATE" -> GuildCreate(decoder.decodeSerializableElement(descriptor, i, Guild.serializer()))
            "GUILD_UPDATE" -> GuildUpdate(decoder.decodeSerializableElement(descriptor, i, Guild.serializer()))
            "GUILD_DELETE" -> GuildDelete(decoder.decodeSerializableElement(descriptor, i, UnavailableGuild.serializer()))
            "GUILD_BAN_ADD" -> GuildBanAdd(decoder.decodeSerializableElement(descriptor, i, GuildBan.serializer()))
            "GUILD_BAN_REMOVE" -> GuildBanRemove(decoder.decodeSerializableElement(descriptor, i, GuildBan.serializer()))
            "GUILD_EMOJIS_UPDATE" -> GuildEmojisUpdate(decoder.decodeSerializableElement(descriptor, i, UpdatedEmojis.serializer()))
            "GUILD_INTEGRATIONS_UPDATE" -> GuildIntegrationsUpdate(decoder.decodeSerializableElement(descriptor, i, GuildIntegrations.serializer()))
            "GUILD_MEMBER_ADD" -> GuildMemberAdd(decoder.decodeSerializableElement(descriptor, i, AddedGuildMember.serializer()))
            "GUILD_MEMBER_REMOVE" -> GuildMemberRemove(decoder.decodeSerializableElement(descriptor, i, RemovedGuildMember.serializer()))
            "GUILD_MEMBER_UPDATE" -> GuildMemberUpdate(decoder.decodeSerializableElement(descriptor, i, UpdatedGuildMember.serializer()))
            "GUILD_ROLE_CREATE" -> GuildRoleCreate(decoder.decodeSerializableElement(descriptor, i, GuildRole.serializer()))
            "GUILD_ROLE_UPDATE" -> GuildRoleUpdate(decoder.decodeSerializableElement(descriptor, i, GuildRole.serializer()))
            "GUILD_ROLE_DELETE" -> GuildRoleDelete(decoder.decodeSerializableElement(descriptor, i, DeletedGuildRole.serializer()))
            "GUILD_MEMBERS_CHUNK" -> GuildMembersChunk(decoder.decodeSerializableElement(descriptor, i, GuildMembersChunkData.serializer()))

            "MESSAGE_CREATE" -> MessageCreate(decoder.decodeSerializableElement(descriptor, i, Message.serializer()))
            "MESSAGE_UPDATE" -> MessageUpdate(decoder.decodeSerializableElement(descriptor, i, Message.serializer()))
            "MESSAGE_DELETE" -> MessageDelete(decoder.decodeSerializableElement(descriptor, i, DeletedMessage.serializer()))
            "MESSAGE_DELETE_BULK" -> MessageDeleteBulk(decoder.decodeSerializableElement(descriptor, i, BulkDeleteData.serializer()))
            "MESSAGE_REACTION_ADD" -> MessageReactionAdd(decoder.decodeSerializableElement(descriptor, i, MessageReaction.serializer()))
            "MESSAGE_REACTION_REMOVE" -> MessageReactionRemove(decoder.decodeSerializableElement(descriptor, i, MessageReaction.serializer()))

            "MESSAGE_REACTION_REMOVE_ALL" -> MessageReactionRemoveAll(decoder.decodeSerializableElement(descriptor, i, AllRemovedMessageReactions.serializer()))
            "PRESENCE_UPDATE" -> PresenceUpdate(decoder.decodeSerializableElement(descriptor, i, PresenceUpdateData.serializer()))
            "USER_UPDATE" -> UserUpdate(decoder.decodeSerializableElement(descriptor, i, User.serializer()))
            "VOICE_STATE_UPDATE" -> VoiceStateUpdate(decoder.decodeSerializableElement(descriptor, i, VoiceState.serializer()))
            "VOICE_SERVER_UPDATE" -> VoiceServerUpdate(decoder.decodeSerializableElement(descriptor, i, VoiceServerUpdateData.serializer()))
            "WEBHOOKS_UPDATE" -> WebhooksUpdate(decoder.decodeSerializableElement(descriptor, i, WebhooksUpdateData.serializer()))
            else -> TODO("log this event $name")
        }
    }

}


