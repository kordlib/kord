package dev.kord.common.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Type to represent a Discord value that can only be null. This class cannot be instantiated.
 */
@Serializable(with = DiscordNull.Serializer::class)
public class DiscordNull private constructor() {

    internal object Serializer : KSerializer<DiscordNull> {

        override val descriptor: SerialDescriptor
            get() = buildClassSerialDescriptor("Kord.DiscordNull")

        override fun deserialize(decoder: Decoder): DiscordNull {
            throw SerializationException("DiscordNull cannot have an instance.")
        }

        override fun serialize(encoder: Encoder, value: DiscordNull) {
            throw SerializationException("DiscordNull cannot be encoded.")
        }
    }
}
