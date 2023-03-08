package dev.kord.common.entity

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.DeprecationLevel.WARNING

/**
 * Type to represent a Discord value that can only be null. This class cannot be instantiated.
 */
@Suppress("DEPRECATION")
@Deprecated(
    "This class is similar to 'Nothing' as it has no instances. The only reason it existed was to have a " +
        "@Serializable version of 'Nothing'. However, since Kotlin 1.8.0 and kotlinx.serialization 1.5.0-RC " +
        "'Nothing' is a serializable class. This means 'DiscordNull' isn't needed anymore and should be replaced " +
        "with 'Nothing'.",
    ReplaceWith("Nothing", imports = ["kotlin.Nothing"]),
    level = WARNING,
)
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
