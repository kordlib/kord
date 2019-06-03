package com.gitlab.hopebaron.websocket

import com.gitlab.hopebaron.websocket.entity.Activity
import com.gitlab.hopebaron.websocket.entity.Shard
import com.gitlab.hopebaron.websocket.entity.Snowflake
import kotlinx.serialization.*
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringDescriptor

sealed class Command {

    companion object : SerializationStrategy<Command> {

        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Command") {
            init {
                addElement("op")
                addElement("d")
            }
        }

        override fun serialize(encoder: Encoder, obj: Command) {
            val composite = encoder.beginStructure(descriptor)
            when (obj) {
                is RequestGuildMembers -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.OpCodeSerializer, OpCode.RequestGuildMembers)
                    composite.encodeSerializableElement(descriptor, 1, RequestGuildMembers.serializer(), obj)
                }
                is UpdateVoiceStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.OpCodeSerializer, OpCode.VoiceStateUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateVoiceStatus.serializer(), obj)
                }
                is UpdateStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.OpCodeSerializer, OpCode.StatusUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateStatus.serializer(), obj)
                }
            }

            composite.endStructure(descriptor)
        }

    }

}


data class Identify(
        internal val token: String,
        val properties: IdentifyProperties,
        val compress: Boolean? = null,
        @SerialName("large_threshold")
        val largeThreshold: Int = 50,
        val shard: Shard? = null,
        val presence: Presence? = null
) {
    override fun toString(): String = "Identify(token=hunter2,properties=$properties,compress=$compress,largeThreshold=$largeThreshold," +
            "shard=$shard,presence=$presence"

    companion object : SerializationStrategy<Identify> {

        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Command") {
            init {
                addElement("op")
                addElement("d")
            }
        }

        override fun serialize(encoder: Encoder, obj: Identify) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeSerializableElement(descriptor, 0, OpCode.OpCodeSerializer, OpCode.Identify)
            composite.encodeSerializableElement(descriptor, 1, Inner, obj)
            composite.endStructure(descriptor)
        }

        private object Inner : SerializationStrategy<Identify> {
            override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Resume") {
                init {
                    addElement("token")
                    addElement("properties")
                    addElement("compress")
                    addElement("large_threshold")
                    addElement("shard")
                    addElement("presence")
                }
            }

            override fun serialize(encoder: Encoder, obj: Identify) {
                val composite = encoder.beginStructure(descriptor)
                composite.encodeStringElement(descriptor, 0, obj.token)
                composite.encodeSerializableElement(descriptor, 1, IdentifyProperties.serializer(), obj.properties)
                composite.encodeNullableSerializableElement(descriptor, 2, Boolean.serializer(), obj.compress)
                composite.encodeIntElement(descriptor, 3, obj.largeThreshold)
                composite.encodeNullableSerializableElement(descriptor, 4, Shard.serializer(), obj.shard)
                composite.encodeNullableSerializableElement(descriptor, 5, Presence.serializer(), obj.presence)
                composite.endStructure(descriptor)
            }
        }

    }

}

@Serializable
data class IdentifyProperties(
        @SerialName("\$os")
        val os: String,
        @SerialName("\$browser")
        val browser: String,
        @SerialName("\$device")
        val device: String
)

@Serializable
data class Presence(
        val status: String,
        val afk: Boolean,
        val since: Int? = null,
        val game: Activity? = null
)


@Serializable
data class Resume(
        val token: String,
        val sessionId: String,
        val sequenceNumber: Long
) {
    override fun toString(): String = "Resume(token=hunter2,sessionId=$sessionId,sequenceNumber:$sequenceNumber)"

    companion object : SerializationStrategy<Resume> {

        override val descriptor: SerialDescriptor = object : SerialClassDescImpl("Command") {
            init {
                addElement("op")
                addElement("d")
            }
        }

        override fun serialize(encoder: Encoder, obj: Resume) {
            val composite = encoder.beginStructure(descriptor)
            composite.encodeSerializableElement(descriptor, 0, OpCode.OpCodeSerializer, OpCode.Resume)
            composite.encodeSerializableElement(descriptor, 1, Inner, obj)
            composite.endStructure(descriptor)
        }

        private object Inner : SerializationStrategy<Resume> {
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
                composite.encodeLongElement(descriptor, 2, obj.sequenceNumber)
                composite.endStructure(descriptor)
            }
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
            composite.encodeNullableSerializableElement(descriptor, 1, Long.serializer(), obj.sequenceNumber)
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
        val since: Long? = null,
        val game: Activity? = null,
        val status: Status,
        val afk: Boolean
) : Command()

@Serializable(with = Status.StatusSerializer::class)
enum class Status {
    Online, DnD, Idle, Invisible, Offline;

    @Serializer(forClass = Status::class)
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