package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class Activity(
        val name: String,
        val type: ActivityType,
        val url: String? = null,
        val timestamps: ActivityTimeStamps? = null,
        @SerialName("application_id")
        val applicationId: String? = null,
        val details: String? = null,
        val state: String? = null,
        val party: ActivityParty? = null,
        val assets: ActivityAssets? = null,
        val secrets: ActivitySecrets? = null,
        val instance: Boolean? = null,
        val flags: Int? = null
)

@Serializable
data class ActivityTimeStamps(
        val start: Long? = null,
        val end: Long? = null
)

@Serializable
data class ActivityParty(
        val id: String? = null,
        val size: List<Int>? = null
)

@Serializable
data class ActivityAssets(
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
data class ActivitySecrets(
        val join: String? = null,
        val spectate: String? = null,
        val match: String? = null
)

@Serializable(with = ActivityType.ActivityTypeSerializer::class)
enum class ActivityType(val code: Int) {
    Game(0),
    Streaming(1),
    Listening(2),
    Watching(3);

    @Serializer(forClass = ActivityType::class)
    companion object ActivityTypeSerializer : KSerializer<ActivityType> {
        override val descriptor: SerialDescriptor
            get() = IntDescriptor.withName("op")

        override fun deserialize(decoder: Decoder): ActivityType {
            val code = decoder.decodeInt()
            return values().first { it.code == code }
        }

        override fun serialize(encoder: Encoder, obj: ActivityType) {
            encoder.encodeInt(obj.code)
        }
    }

}