package com.gitlab.kordlib.common.entity

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.jsonArray

@Serializable
data class DiscordShard(val index: Int, val count: Int) {

    @Serializer(forClass = DiscordShard::class)
    companion object : KSerializer<DiscordShard> {

        override fun serialize(encoder: Encoder, obj: DiscordShard) {
            val array = jsonArray {
                +JsonLiteral(obj.index)
                +JsonLiteral(obj.count)
            }

            encoder.encode(JsonArray.serializer(), array)
        }

        override fun deserialize(decoder: Decoder): DiscordShard {
            val array = JsonArray.serializer().deserialize(decoder)
            val index = array.getPrimitive(0).int
            val count = array.getPrimitive(1).int
            return DiscordShard(index, count)
        }

    }

}
