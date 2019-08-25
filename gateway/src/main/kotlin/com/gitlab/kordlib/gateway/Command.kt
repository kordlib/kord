package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.entity.Activity
import com.gitlab.kordlib.common.entity.Shard
import com.gitlab.kordlib.common.entity.Status
import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor
import kotlinx.serialization.internal.SerialClassDescImpl
import kotlinx.serialization.internal.StringDescriptor

sealed class Command {
    internal data class Heartbeat(val sequenceNumber: Int? = null) : Command() {

        companion object : SerializationStrategy<Heartbeat> {
            override val descriptor: SerialDescriptor = IntDescriptor.withName("Heartbeat")

            override fun serialize(encoder: Encoder, obj: Heartbeat) {
                encoder.encodeNullableSerializableValue(Int.serializer(), obj.sequenceNumber)
            }
        }

    }

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
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.RequestGuildMembers)
                    composite.encodeSerializableElement(descriptor, 1, RequestGuildMembers.serializer(), obj)
                }
                is UpdateVoiceStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.VoiceStateUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateVoiceStatus.serializer(), obj)
                }
                is UpdateStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.StatusUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateStatus.serializer(), obj)
                }
                is Identify -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, Identify.serializer(), obj)
                }
                is Resume -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, Resume.serializer(), obj)
                }
                is Heartbeat -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Heartbeat)
                    composite.encodeSerializableElement(descriptor, 1, Heartbeat.Companion, obj)
                }
            }

            composite.endStructure(descriptor)
        }

    }

}


@Serializable
internal data class Identify(
        internal val token: String,
        val properties: IdentifyProperties,
        val compress: Boolean? = null,
        @SerialName("large_threshold")
        val largeThreshold: Int = 50,
        val shard: Shard? = null,
        val presence: Presence? = null
) : Command() {
    override fun toString(): String = "Identify(token=hunter2,properties=$properties,compress=$compress,largeThreshold=$largeThreshold," +
            "shard=$shard,presence=$presence"
}

@Serializable
data class IdentifyProperties(
        @Required
        @SerialName("\$os")
        val os: String,
        @SerialName("\$browser")
        val browser: String,
        @SerialName("\$device")
        val device: String
)

@Serializable
data class Presence(
        val status: Status,
        val afk: Boolean,
        val since: Int? = null,
        val game: Activity? = null
)

@Serializable
internal data class Resume(
        val token: String,
        @SerialName("session_id")
        val sessionId: String,
        @SerialName("seq")
        val sequenceNumber: Int?
) : Command() {
    override fun toString(): String = "Resume(token=hunter2,sessionId=$sessionId,sequenceNumber:$sequenceNumber)"
}

@Serializable
data class RequestGuildMembers(
        @SerialName("guild_id")
        val guildId: String,
        val query: String,
        val limit: Int = 0
) : Command()

@Serializable
data class UpdateVoiceStatus(
        @SerialName("guild_id")
        val guildId: String,
        @SerialName("channel_id")
        val channelId: String? = null,
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
