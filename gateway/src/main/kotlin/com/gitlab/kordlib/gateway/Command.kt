package com.gitlab.kordlib.gateway

import com.gitlab.kordlib.common.entity.*
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalBoolean
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import kotlinx.serialization.*
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject

sealed class Command {
    internal data class Heartbeat(val sequenceNumber: Int? = null) : Command() {

        companion object : SerializationStrategy<Heartbeat> {
            override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Heartbeat", PrimitiveKind.INT)

            @OptIn(ExperimentalSerializationApi::class)
            override fun serialize(encoder: Encoder, value: Heartbeat) {
                encoder.encodeNullableSerializableValue(Int.serializer(), value.sequenceNumber)
            }
        }

    }

    companion object : SerializationStrategy<Command> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Command") {
            element("op", OpCode.descriptor)
            element("d", JsonObject.serializer().descriptor)
        }

        override fun serialize(encoder: Encoder, value: Command) {
            val composite = encoder.beginStructure(descriptor)
            when (value) {
                is RequestGuildMembers -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.RequestGuildMembers)
                    composite.encodeSerializableElement(descriptor, 1, RequestGuildMembers.serializer(), value)
                }
                is UpdateVoiceStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.VoiceStateUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateVoiceStatus.serializer(), value)
                }
                is UpdateStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.StatusUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateStatus.serializer(), value)
                }
                is Identify -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, Identify.serializer(), value)
                }
                is Resume -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, Resume.serializer(), value)
                }
                is Heartbeat -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode, OpCode.Heartbeat)
                    composite.encodeSerializableElement(descriptor, 1, Heartbeat.Companion, value)
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
        val compress: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("large_threshold")
        val largeThreshold: OptionalInt = OptionalInt.Missing,
        val shard: Optional<DiscordShard> = Optional.Missing(),
        val presence: Optional<DiscordPresence> = Optional.Missing(),
        val intents: Intents,
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
        val device: String,
)

@Serializable
data class GuildMembersChunkData(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val members: List<DiscordGuildMember>,
        @SerialName("chunk_index")
        val chunkIndex: Int,
        @SerialName("chunk_count")
        val chunkCount: Int,
        @SerialName("not_found")
        val notFound: Optional<List<String>> = Optional.Missing(),
        val presences: Optional<List<DiscordPresenceUpdate>> = Optional.Missing(),
        val nonce: String
)

@Serializable
data class DiscordPresence(
        val status: PresenceStatus,
        val afk: Boolean,
        val since: Long? = null,
        val game: DiscordBotActivity? = null,
)

@Serializable
internal data class Resume(
        val token: String,
        @SerialName("session_id")
        val sessionId: String,
        @SerialName("seq")
        val sequenceNumber: Int,
) : Command() {
    override fun toString(): String = "Resume(token=hunter2,sessionId=$sessionId,sequenceNumber:$sequenceNumber)"
}

@Serializable
data class RequestGuildMembers(
        @SerialName("guild_id")
        val guildId: Snowflake,
        val query: Optional<String> = Optional.Missing(),
        val limit: Int,
        val presences: OptionalBoolean = OptionalBoolean.Missing,
        @SerialName("user_ids")
        val userIds: Optional<List<Snowflake>> = Optional.Missing(),
        val nonce: Optional<String> = Optional.Missing()
) : Command()

@Serializable
data class UpdateVoiceStatus(
        @SerialName("guild_id")
        val guildId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("self_mute")
        val selfMute: Boolean,
        @SerialName("self_deaf")
        val selfDeaf: Boolean,
) : Command()

@Serializable
data class UpdateStatus(
        val since: Long?,
        val activities: List<DiscordBotActivity>?,
        val status: PresenceStatus,
        val afk: Boolean,
) : Command()
