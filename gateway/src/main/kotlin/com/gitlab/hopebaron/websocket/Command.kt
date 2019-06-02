package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.Activity
import com.gitlab.hopebaron.websocket.entity.Snowflake
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringDescriptor

sealed class Command

data class Resume(val token: String, val sessionId: String, val sequenceNumber: Long) {
    override fun toString(): String = "Resume(token=hunter2,sessionId=$sessionId,sequenceNumber:$sequenceNumber)"

    companion object : SerializationStrategy<Resume> {
        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Resume") {
            init {
                addElement("token")
                addElement("session_id")
                addElement("seq")
            }
        }

        override fun serialize(encoder: Encoder, obj: Resume) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeStringElement(descriptor, 0, obj.token)
            composite.encodeStringElement(descriptor, 1, obj.sessionId)
            composite.encodeLongElement(descriptor, 0, obj.sequenceNumber)
            composite.endStructure(descriptor)
        }

    }

}


data class Heartbeat(val sequenceNumber: Long? = null) {

    companion object : SerializationStrategy<Heartbeat> {
        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Heartbeat") {
            init {
                addElement("op")
                addElement("d", isOptional = true)
            }
        }

        override fun serialize(encoder: Encoder, obj: Heartbeat) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeIntElement(descriptor, 0, OpCode.Heartbeat.code)
            obj.sequenceNumber?.let {
                composite.encodeLongElement(descriptor, 1, it)
            }
            composite.endStructure(descriptor)
        }
    }

}

@Serializable
data class RequestGuildMembers(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val query: String,
        val limit: Int = 0
) : Command()

@Serializable
data class UpdateVoiceStatus(
        @SerialName("guild_id")
        val guildId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake? = null,
        @SerialName("self_mute")
        val selfMute: Boolean,
        @SerialName("self_deaf")
        val selfDeaf: Boolean
) : Command()

@Serializable
data class UpdateStatus(
        val since: Int? = null,
        val game: Activity? = null,
        val status: Status,
        val afk: Boolean
) : Command()

@Serializable
enum class Status {
    Online, DnD, Idle, Invisible, Offline;

    companion object StatusSerializer : KSerializer<Status> {
        override val descriptor: SerialDescriptor = StringDescriptor.withName("Status")

        override fun deserialize(decoder: Decoder): Status {
            val name = decoder.decodeString()
            return values().first { it.name.toLowerCase() == name }
        }

        override fun serialize(encoder: Encoder, obj: Status) {
            encoder.encodeString(obj.name.toLowerCase())
        }
    }
}