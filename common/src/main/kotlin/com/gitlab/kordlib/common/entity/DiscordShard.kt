package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * An instance of a [Discord shard](https://discord.com/developers/docs/topics/gateway#sharding).
 */
@Serializable
data class DiscordShard(val index: Int, val count: Int) {

    @Serializer(forClass = DiscordShard::class)
    companion object : KSerializer<DiscordShard> {

        override fun serialize(encoder: Encoder, obj: DiscordShard) {
            val array = buildJsonArray {
                add(JsonPrimitive(obj.index))
                add(JsonPrimitive(obj.count))
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
