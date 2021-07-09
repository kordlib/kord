package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordThreadMember
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadUserData(
    val id: Snowflake,
    @SerialName("user_id")
    val userId: Snowflake,
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
