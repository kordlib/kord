package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.InstantInEpochMillisecondsSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
public data class DiscordBotActivity(
    val name: String,
    val type: ActivityType,
    val url: Optional<String?> = Optional.Missing()
)

@Serializable
public data class DiscordActivity(
    val name: String,
    val type: ActivityType,
    val url: Optional<String?> = Optional.Missing(),
    @SerialName("created_at")
    @Serializable(with = InstantInEpochMillisecondsSerializer::class)
    val createdAt: Instant,
    val timestamps: Optional<DiscordActivityTimestamps> = Optional.Missing(),
    @SerialName("application_id")
    val applicationId: OptionalSnowflake = OptionalSnowflake.Missing,
    val details: Optional<String?> = Optional.Missing(),
    val state: Optional<String?> = Optional.Missing(),
    val emoji: Optional<DiscordActivityEmoji?> = Optional.Missing(),
    val party: Optional<DiscordActivityParty> = Optional.Missing(),
    val assets: Optional<DiscordActivityAssets> = Optional.Missing(),
    val secrets: Optional<DiscordActivitySecrets> = Optional.Missing(),
    val instance: OptionalBoolean = OptionalBoolean.Missing,
    val flags: Optional<ActivityFlags> = Optional.Missing(),
    val buttons: Optional<List<String>> = Optional.Missing()
)

public enum class ActivityFlag(public val value: Int) {
    Instance(1),
    Join(2),
    Spectate(4),
    JoinRequest(8),
    Sync(16),
    Play(32)
}

@Serializable(with = ActivityFlags.Serializer::class)
public class ActivityFlags(public val value: Int) {

    public val flags: Set<ActivityFlag>
        get() = ActivityFlag.values().filter { (it.value and value) == it.value }.toSet()

    public operator fun contains(flag: ActivityFlag): Boolean = (flag.value and value) == flag.value

    internal object Serializer : KSerializer<ActivityFlags> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("Kord.ActivityFlags", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ActivityFlags = ActivityFlags(decoder.decodeInt())

        override fun serialize(encoder: Encoder, value: ActivityFlags) {
            encoder.encodeInt(value.value)
        }
    }
}

@Deprecated(
    "DiscordActivityTimeStamps was renamed to DiscordActivityTimestamps.",
    ReplaceWith("DiscordActivityTimestamps"),
    DeprecationLevel.ERROR,
)
public typealias DiscordActivityTimeStamps = DiscordActivityTimestamps

@Serializable
public data class DiscordActivityTimestamps(
    val start: Optional<@Serializable(with = InstantInEpochMillisecondsSerializer::class) Instant> = Optional.Missing(),
    val end: Optional<@Serializable(with = InstantInEpochMillisecondsSerializer::class) Instant> = Optional.Missing(),
)

@Serializable
public data class DiscordActivityEmoji(
    val name: String,
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    val animated: OptionalBoolean = OptionalBoolean.Missing
)

@Serializable
public data class DiscordActivityParty(
    val id: Optional<String> = Optional.Missing(),
    val size: Optional<DiscordActivityPartySize> = Optional.Missing()
)

@Serializable(DiscordActivityPartySize.Serializer::class)
public data class DiscordActivityPartySize(
    val current: Int,
    val maximum: Int
) {
    internal object Serializer : KSerializer<DiscordActivityPartySize> {
        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = listSerialDescriptor(Int.serializer().descriptor)

        private val delegate = ListSerializer(Int.serializer())

        override fun deserialize(decoder: Decoder): DiscordActivityPartySize {
            val (current, maximum) = delegate.deserialize(decoder)
            return DiscordActivityPartySize(current, maximum)
        }

        override fun serialize(encoder: Encoder, value: DiscordActivityPartySize) {
            delegate.serialize(encoder, listOf(value.current, value.maximum))
        }
    }
}

@Serializable
public data class DiscordActivityAssets(
    @SerialName("large_image")
    val largeImage: Optional<String> = Optional.Missing(),
    @SerialName("large_text")
    val largeText: Optional<String> = Optional.Missing(),
    @SerialName("small_image")
    val smallImage: Optional<String> = Optional.Missing(),
    @SerialName("small_text")
    val smallText: Optional<String> = Optional.Missing()
)

@Serializable
public data class DiscordActivitySecrets(
    val join: Optional<String> = Optional.Missing(),
    val spectate: Optional<String> = Optional.Missing(),
    val match: Optional<String> = Optional.Missing()
)

@Serializable(with = ActivityType.ActivityTypeSerializer::class)
public enum class ActivityType(public val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    Game(0),
    Streaming(1),
    Listening(2),
    Watching(3),
    Custom(4),
    Competing(5);

    public companion object ActivityTypeSerializer : KSerializer<ActivityType> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor("op", PrimitiveKind.INT)

        override fun deserialize(decoder: Decoder): ActivityType {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, value: ActivityType) {
            encoder.encodeInt(value.code)
        }
    }

}
