package dev.kord.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * An instance of a [Discord shard](https://discord.com/developers/docs/topics/gateway#sharding).
 */
@Serializable(with = DiscordShard.Companion::class)
public data class DiscordShard(val index: Int, val count: Int) {

    public companion object : KSerializer<DiscordShard> {

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor
            get() = listSerialDescriptor(PrimitiveSerialDescriptor("DiscordShardElement", PrimitiveKind.INT))

        override fun serialize(encoder: Encoder, value: DiscordShard) {
            val array = buildJsonArray {
                add(JsonPrimitive(value.index))
                add(JsonPrimitive(value.count))
            }

            encoder.encodeSerializableValue(JsonArray.serializer(), array)
        }

        override fun deserialize(decoder: Decoder): DiscordShard {
            val array = JsonArray.serializer().deserialize(decoder)
            val index = array[0].jsonPrimitive.int
            val count = array[1].jsonPrimitive.int
            return DiscordShard(index, count)
        }

    }

}
