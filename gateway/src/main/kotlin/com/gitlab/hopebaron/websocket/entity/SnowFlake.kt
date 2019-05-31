package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor
import java.time.Instant

const val discordEpoch = 1420070400000

@Serializable
class SnowFlake(private val id: String) {
    @Serializer(SnowFlake::class)
    companion object : KSerializer<SnowFlake> {
        override val descriptor: SerialDescriptor
            get() = StringDescriptor.withName("id")

        override fun deserialize(decoder: Decoder): SnowFlake {
            val snowFlake = decoder.decodeString()
            return SnowFlake(snowFlake)
        }

        override fun serialize(encoder: Encoder, obj: SnowFlake) {
            val timestamp = obj.instant.toEpochMilli()
            val snowFlake = (timestamp - discordEpoch) shr 22
            encoder.encodeString(snowFlake.toString())
        }
    }

    val idLong
        get() = id.toLong()
    val instant: Instant
        get() {
            val time = idLong shl 22
            return Instant.ofEpochMilli(discordEpoch + time)
        }
}