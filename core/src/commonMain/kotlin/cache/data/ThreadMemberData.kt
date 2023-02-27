package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordThreadMember
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ThreadMemberData(
    val id: Snowflake,
    @SerialName("user_id")
    val userId: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("join_timestamp")
    val joinTimestamp: Instant,
    val flags: Int
) {
    public companion object {
        public val description: DataDescription<ThreadMemberData, Snowflake> = description(ThreadMemberData::id)


        public fun from(data: DiscordThreadMember, thread: Snowflake? = null): ThreadMemberData =
            with(data) {
                val id = this.id.value ?: thread!!
                ThreadMemberData(id, userId, joinTimestamp, flags)
            }
    }
}

public fun DiscordThreadMember.toData(threadId: Snowflake?): ThreadMemberData {
    return ThreadMemberData.from(this, threadId)
}
