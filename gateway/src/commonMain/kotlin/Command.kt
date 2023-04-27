package dev.kord.gateway

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.serialization.InstantInEpochMillisecondsSerializer
import kotlinx.atomicfu.atomic
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement

@Serializable(with = Command.SerializationStrategy::class)
public sealed class Command {

    public data class Heartbeat(val sequenceNumber: Int?) : Command()

    public object SerializationStrategy : KSerializer<Command> {

        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Command") {
            element("op", OpCode.serializer().descriptor)
            element("d", JsonElement.serializer().descriptor)
        }

        override fun deserialize(decoder: Decoder): Command =
            TODO("Deserializing gateway commands is not supported yet")

        @OptIn(PrivilegedIntent::class)
        override fun serialize(encoder: Encoder, value: Command) {
            val composite = encoder.beginStructure(descriptor)
            when (value) {
                is RequestGuildMembers -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.RequestGuildMembers)
                    composite.encodeSerializableElement(descriptor, 1, RequestGuildMembers.serializer(), value)
                }
                is UpdateVoiceStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.VoiceStateUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateVoiceStatus.serializer(), value)
                }
                is UpdateStatus -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.StatusUpdate)
                    composite.encodeSerializableElement(descriptor, 1, UpdateStatus.serializer(), value)
                }
                is Identify -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Identify)
                    composite.encodeSerializableElement(descriptor, 1, Identify.serializer(), value)
                }
                is Resume -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Resume)
                    composite.encodeSerializableElement(descriptor, 1, Resume.serializer(), value)
                }
                is Heartbeat -> {
                    composite.encodeSerializableElement(descriptor, 0, OpCode.serializer(), OpCode.Heartbeat)
                    @OptIn(ExperimentalSerializationApi::class)
                    composite.encodeNullableSerializableElement(descriptor, 1, Int.serializer(), value.sequenceNumber)
                }
            }

            composite.endStructure(descriptor)
        }

    }

}


@Serializable
public data class Identify(
    val token: String,
    val properties: IdentifyProperties,
    val compress: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("large_threshold")
    val largeThreshold: OptionalInt = OptionalInt.Missing,
    val shard: Optional<DiscordShard> = Optional.Missing(),
    val presence: Optional<DiscordPresence> = Optional.Missing(),
    val intents: Intents,
) : Command() {
    override fun toString(): String =
        "Identify(token=hunter2, properties=$properties, compress=$compress, largeThreshold=$largeThreshold, " +
                "shard=$shard, presence=$presence, intents=$intents)"
}

@Serializable
public data class IdentifyProperties(
    val os: String,
    val browser: String,
    val device: String,
)

@Serializable
public data class GuildMembersChunkData(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val members: List<DiscordGuildMember>,
    @SerialName("chunk_index")
    val chunkIndex: Int,
    @SerialName("chunk_count")
    val chunkCount: Int,
    @SerialName("not_found")
    val notFound: Optional<Set<Snowflake>> = Optional.Missing(),
    val presences: Optional<List<DiscordPresenceUpdate>> = Optional.Missing(),
    val nonce: Optional<String> = Optional.Missing()
)

@Serializable
public data class DiscordPresence(
    val status: PresenceStatus,
    val afk: Boolean,
    @Serializable(with = InstantInEpochMillisecondsSerializer::class)
    val since: Instant? = null,
    val game: DiscordBotActivity? = null,
)

@Serializable
public data class Resume(
    val token: String,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("seq")
    val sequenceNumber: Int,
) : Command() {
    override fun toString(): String = "Resume(token=hunter2, sessionId=$sessionId, sequenceNumber=$sequenceNumber)"
}

/**
 * A representation of the
 * [Discord Request Guild Members command](https://discord.com/developers/docs/topics/gateway#request-guild-members).
 *
 * When connecting to a [Gateway] Discord will send members up to [Identify.largeThreshold], any additional
 * members can be requested via this command. Sending this command will result in a variable amount of
 * [GuildMembersChunk] events being send until all requested members have been returned.
 *
 * While usage of this command isn't strictly limited to [privileged intents][PrivilegedIntent],
 * certain combinations are:
 * - [Intent.GuildPresences] is required to enable [presences].
 * - [Intent.GuildMembers] is required when setting the [query] to `""` and [limit] to `0`.
 *
 * Other notable behavior:
 * - Requesting a [query] that is not empty (and not [Optional.Missing]) will coerce [limit] to a max of `100`.
 * - [userIds] can only contain a maximum of `100` ids.
 *
 * @param guildId id of the guild on which to execute the command.
 * @param query prefix to match usernames against. Use an empty string to match against all members.
 * @param limit maximum number of members to match against when using a [query]. Use `0` to request all members.
 * @param presences Whether [GuildMembersChunkData.presences] should be present in the response.
 * @param userIds The ids of the user to match against.
 * @param nonce A nonce to identify the [GuildMembersChunkData.nonce] responses.
 */
@PrivilegedIntent
@Serializable
public data class RequestGuildMembers(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val query: Optional<String> = Optional.Missing(),
    val limit: OptionalInt = OptionalInt.Missing,
    val presences: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("user_ids")
    val userIds: Optional<Set<Snowflake>> = Optional.Missing(),
    val nonce: Optional<String> = Optional.Missing()
) : Command() {

    public object Nonce {
        private val counter = atomic(0)

        public fun new(): String = counter.getAndIncrement().toUInt().toString()

    }

}

@Serializable
public data class UpdateVoiceStatus(
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("channel_id")
    val channelId: Snowflake?,
    @SerialName("self_mute")
    val selfMute: Boolean,
    @SerialName("self_deaf")
    val selfDeaf: Boolean,
) : Command()

@Serializable
public data class UpdateStatus(
    @Serializable(with = InstantInEpochMillisecondsSerializer::class)
    val since: Instant?,
    val activities: List<DiscordBotActivity>,
    val status: PresenceStatus,
    val afk: Boolean,
) : Command()
