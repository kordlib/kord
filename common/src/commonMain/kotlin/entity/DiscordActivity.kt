@file:GenerateKordEnum(
    name = "ActivityType", valueType = INT, valueName = "code",
    docUrl = "https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-types",
    entries = [
        Entry("Game", intValue = 0),
        Entry("Streaming", intValue = 1),
        Entry("Listening", intValue = 2),
        Entry("Watching", intValue = 3),
        Entry("Custom", intValue = 4),
        Entry("Competing", intValue = 5),
    ],
)

@file:GenerateKordEnum(
    name = "ActivityFlag",
    valueType = GenerateKordEnum.ValueType.INT,
    isFlags = true,
    docUrl = "https://discord.com/developers/docs/topics/gateway-events#activity-object-activity-flags",
    entries = [
        Entry(name = "Instance", intValue = 1 shl 0),
        Entry(name = "Join", intValue = 1 shl 1),
        Entry(name = "Spectate", intValue = 1 shl 2),
        Entry(name = "JoinRequest", intValue = 1 shl 3),
        Entry(name = "Sync", intValue = 1 shl 4),
        Entry(name = "Play", intValue = 1 shl 5),
        Entry(name = "PartyPrivacyFriends", intValue = 1 shl 6),
        Entry(name = "PartyPrivacVoiceChannel", intValue = 1 shl 7),
        Entry(name = "Embed", intValue = 1 shl 8),
    ],
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.InstantInEpochMilliseconds
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
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
    val createdAt: InstantInEpochMilliseconds,
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

@Serializable
public data class DiscordActivityTimestamps(
    val start: Optional<InstantInEpochMilliseconds> = Optional.Missing(),
    val end: Optional<InstantInEpochMilliseconds> = Optional.Missing(),
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
