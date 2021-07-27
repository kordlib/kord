package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordThreadMember
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadUserData(
    val id: Snowflake,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("join_timestamp")
    val joinTimestamp: String,
    val flags: Int
) {
    companion object {
        fun from(data: DiscordThreadMember, thread: Snowflake? = null): ThreadUserData =
            with(data) {
                val id = this.id.value ?: thread!!
                ThreadUserData(id, userId, joinTimestamp, flags)
            }
    }
}
