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
import kotlin.DeprecationLevel.HIDDEN
import kotlin.LazyThreadSafetyMode.PUBLICATION
import kotlin.jvm.JvmField
import kotlin.jvm.JvmStatic

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


// TODO replace with the following annotation once the deprecation cycle for enum artifacts is done
// @file:GenerateKordEnum(
//     name = "ActivityType", valueType = INT, valueName = "code",
//     deprecatedSerializerName = "ActivityTypeSerializer",
//     docUrl = "https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-types",
//     entries = [
//         Entry("Game", intValue = 0),
//         Entry("Streaming", intValue = 1),
//         Entry("Listening", intValue = 2),
//         Entry("Watching", intValue = 3),
//         Entry("Custom", intValue = 4),
//         Entry("Competing", intValue = 5),
//     ],
// )
@Serializable(with = ActivityType.Serializer::class)
public sealed class ActivityType(public val code: Int) {
    final override fun equals(other: Any?): Boolean =
        this === other || (other is ActivityType && this.code == other.code)

    final override fun hashCode(): Int = code.hashCode()
    final override fun toString(): String = "ActivityType.${this::class.simpleName}(code=$code)"

    public class Unknown(code: Int) : ActivityType(code)
    public object Game : ActivityType(0)
    public object Streaming : ActivityType(1)
    public object Listening : ActivityType(2)
    public object Watching : ActivityType(3)
    public object Custom : ActivityType(4)
    public object Competing : ActivityType(5)

    internal object Serializer : KSerializer<ActivityType> {
        override val descriptor = PrimitiveSerialDescriptor("dev.kord.common.entity.ActivityType", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: ActivityType) = encoder.encodeInt(value.code)
        override fun deserialize(decoder: Decoder) = when (val code = decoder.decodeInt()) {
            0 -> Game
            1 -> Streaming
            2 -> Listening
            3 -> Watching
            4 -> Custom
            5 -> Competing
            else -> Unknown(code)
        }
    }

    public companion object {
        public val entries: List<ActivityType> by lazy(mode = PUBLICATION) {
            listOf(Game, Streaming, Listening, Watching, Custom, Competing)
        }


        // enum artifacts

        private val UNKNOWN = Unknown(Int.MIN_VALUE) // like old enum entry `Unknown`

        // @formatter:off
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField public val Unknown: ActivityType = UNKNOWN
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField public val Game: ActivityType = Game
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField public val Streaming: ActivityType = Streaming
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField public val Listening: ActivityType = Listening
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField public val Watching: ActivityType = Watching
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField public val Custom: ActivityType = Custom
        @Deprecated("Binary compatibility", level = HIDDEN) @JvmField
        public val Competing: ActivityType = Competing
        // @formatter:on

        /** @suppress */
        @Suppress("NON_FINAL_MEMBER_IN_OBJECT")
        @Deprecated("ActivityType is no longer an enum class. Deprecated without replacement.", level = HIDDEN)
        @JvmStatic
        public open fun valueOf(name: String): ActivityType = when (name) {
            "Unknown" -> UNKNOWN
            "Game" -> Game
            "Streaming" -> Streaming
            "Listening" -> Listening
            "Watching" -> Watching
            "Custom" -> Custom
            "Competing" -> Competing
            else -> throw IllegalArgumentException(name)
        }

        /** @suppress */
        @Suppress("NON_FINAL_MEMBER_IN_OBJECT")
        @Deprecated(
            "ActivityType is no longer an enum class.",
            ReplaceWith("ActivityType.entries.toTypedArray()", "dev.kord.common.entity.ActivityType"),
            level = HIDDEN,
        )
        @JvmStatic
        public open fun values(): Array<ActivityType> =
            arrayOf(UNKNOWN, Game, Streaming, Listening, Watching, Custom, Competing)


        @Suppress("DEPRECATION_ERROR")
        @Deprecated("Binary compatibility", level = HIDDEN)
        @JvmField
        public val ActivityTypeSerializer: ActivityTypeSerializer = ActivityTypeSerializer
    }

    @Deprecated(
        "Use 'ActivityType.serializer()' instead.",
        ReplaceWith("ActivityType.serializer()", "dev.kord.common.entity.ActivityType"),
        level = HIDDEN,
    )
    public object ActivityTypeSerializer : KSerializer<ActivityType> by Serializer {
        @Deprecated(
            "Use 'ActivityType.serializer()' instead.",
            ReplaceWith("ActivityType.serializer()", "dev.kord.common.entity.ActivityType"),
            level = HIDDEN,
        )
        public fun serializer(): KSerializer<ActivityType> = this
    }
}
