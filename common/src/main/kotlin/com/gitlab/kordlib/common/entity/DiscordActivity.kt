package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class DiscordActivity(
        val name: String,
        val type: ActivityType,
        val url: String? = null,
        val timestamps: DiscordActivityTimeStamps? = null,
        @SerialName("application_id")
        val applicationId: String? = null,
        val details: String? = null,
        val emoji: DiscordPartialEmoji? = null,
        val state: String? = null,
        val party: DiscordActivityParty? = null,
        val assets: DiscordActivityAssets? = null,
        val secrets: DiscordActivitySecrets? = null,
        val instance: Boolean? = null,
        val flags: Int? = null
)

@Serializable
data class DiscordActivityTimeStamps(
        val start: Long? = null,
        val end: Long? = null
)

@Serializable
data class DiscordActivityParty(
        val id: String? = null,
        val size: List<Int>? = null
)

@Serializable
data class DiscordActivityAssets(
        @SerialName("large_image")
        val largeImage: String? = null,
        @SerialName("large_text")
        val largeText: String? = null,
        @SerialName("small_image")
        val smallImage: String? = null,
        @SerialName("small_text")
        val smallText: String? = null
)

@Serializable
data class DiscordActivitySecrets(
        val join: String? = null,
        val spectate: String? = null,
        val match: String? = null
)

@Serializable(with = ActivityType.ActivityTypeSerializer::class)
enum class ActivityType(val code: Int) {
    /** The default code for unknown values. */
    Unknown(Int.MIN_VALUE),
    Game(0),
    Streaming(1),
    Listening(2),
    Watching(3),
    Custom(4);

    @Serializer(forClass = ActivityType::class)
    companion object ActivityTypeSerializer : KSerializer<ActivityType> {
        override val descriptor: SerialDescriptor
            get() = IntDescriptor.withName("op")

        override fun deserialize(decoder: Decoder): ActivityType {
            val code = decoder.decodeInt()
            return values().firstOrNull { it.code == code } ?: Unknown
        }

        override fun serialize(encoder: Encoder, obj: ActivityType) {
            encoder.encodeInt(obj.code)
        }
    }

}