package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.time.Instant

const val discordEpoch = 1420070400000

@Serializable
data class Snowflake(val id: String) {
    @Serializer(Snowflake::class)
    companion object : KSerializer<Snowflake> {
        override val descriptor: SerialDescriptor
            get() = StringDescriptor.withName("id")

        override fun deserialize(decoder: Decoder): Snowflake {
            val snowFlake = decoder.decodeString()
            return Snowflake(snowFlake)
        }

        override fun serialize(encoder: Encoder, obj: Snowflake) =
                encoder.encodeString(obj.id)

    }

    val idLong
        get() = id.toLong()

}

fun Snowflake.instant(): Instant {
    val time = idLong shl 22
    return Instant.ofEpochMilli(discordEpoch + time)
}