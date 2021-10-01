package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordThreadMember
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ThreadMemberData(
    val id: Snowflake,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("join_timestamp")
    val joinTimestamp: String,
    val flags: Int
) {
    companion object {
        val description = description(ThreadMemberData::id)


        fun from(data: DiscordThreadMember, thread: Snowflake? = null): ThreadMemberData =
            with(data) {
                val id = this.id.value ?: thread!!
                ThreadMemberData(id, userId, joinTimestamp, flags)
            }
    }
}

fun DiscordThreadMember.toData(threadId: Snowflake?): ThreadMemberData {
    return ThreadMemberData.from(this, threadId)
}