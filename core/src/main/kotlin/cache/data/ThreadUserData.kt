package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordThreadMember
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadUserData(
    val id: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("join_timestamp")
    val joinTimestamp: String,
    val flags: Int
) {
    companion object {
        fun from(data: DiscordThreadMember): ThreadUserData = with(data) {
            ThreadUserData(id, userId, joinTimestamp, flags)
        }
    }
}
