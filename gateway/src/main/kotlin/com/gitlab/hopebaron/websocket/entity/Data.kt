package com.gitlab.hopebaron.websocket.entity

import kotlinx.serialization.*
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonLiteral
import kotlinx.serialization.json.jsonArray

@Serializable
data class Shard(val index: Int, val count: Int) {

    @Serializer(forClass = Shard::class)
    companion object : KSerializer<Shard> {

        override fun serialize(encoder: Encoder, obj: Shard) {
            val array = jsonArray {
                +JsonLiteral(obj.index)
                +JsonLiteral(obj.count)
            }

            encoder.encode(JsonArray.serializer(), array)
        }

        override fun deserialize(decoder: Decoder): Shard {
            val array = JsonArray.serializer().deserialize(decoder)
            val index = array.getPrimitive(0).int
            val count = array.getPrimitive(1).int
            return Shard(index, count)
        }

    }

}

@Serializable
data class Hello(
        @SerialName("heartbeat_interval")
        val heartbeatInterval: Long,
        @SerialName("_trace")
        val traces: List<String>
)



@Serializable
data class Overwrite(
        val id: Snowflake,
        val type: String,
        val allow: Int,
        val deny: Int
)

@Serializable
data class Resumed(
        val token: String,
        @SerialName("session_id")
        val sessionId: Snowflake,
        @SerialName("seq")
        val sequence: String
)

@Serializable
data class PinsUpdateData(
        @SerialName("guild_id")
        val guildId: Snowflake,
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("last_pin_timestamp")
        val lastPinTimestamp: String? = null
)


@Serializable
data class Typing(
        @SerialName("channel_id")
        val channelId: Snowflake,
        @SerialName("guild_id")
        val guildId: Snowflake? = null,
        @SerialName("user_id")
        val userId: Snowflake,
        val timestamp: Long
)

