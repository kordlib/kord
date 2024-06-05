package dev.kord.common.entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.builtins.IntArraySerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmField

/**
 * An instance of a [Discord shard](https://discord.com/developers/docs/topics/gateway#sharding).
 */
@Serializable(with = DiscordShard.Serializer::class)
public data class DiscordShard(val index: Int, val count: Int) {
    internal object Serializer : KSerializer<DiscordShard> {
        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor =
            SerialDescriptor("dev.kord.common.entity.DiscordShard", original = IntArraySerializer().descriptor)

        override fun serialize(encoder: Encoder, value: DiscordShard) {
            val array = intArrayOf(value.index, value.count)
            encoder.encodeSerializableValue(IntArraySerializer(), array)
        }

        override fun deserialize(decoder: Decoder): DiscordShard {
            val array = decoder.decodeSerializableValue(IntArraySerializer())
            if (array.size != 2) throw SerializationException("Expected IntArray with exactly two elements")
            return DiscordShard(index = array[0], count = array[1])
        }
    }

    public companion object {
        @Suppress("DEPRECATION_ERROR")
        @Deprecated(
            "Renamed to 'Companion'.",
            ReplaceWith("DiscordShard.Companion", imports = ["dev.kord.common.entity.DiscordShard"]),
            DeprecationLevel.ERROR,
        )
        @JvmField
        public val NewCompanion: NewCompanion = NewCompanion()
    }

    @Deprecated(
        "Renamed to 'Companion'.",
        ReplaceWith("DiscordShard.Companion", imports = ["dev.kord.common.entity.DiscordShard"]),
        DeprecationLevel.ERROR,
    )
    public class NewCompanion internal constructor() {
        public fun serializer(): KSerializer<DiscordShard> = DiscordShard.serializer()
    }
}
