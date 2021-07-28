package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.gateway.ThreadListSync

class ThreadListSyncData(
    val guildId: Snowflake,
    val channelIds: Optional<List<Snowflake>> = Optional.Missing(),
    val threads: List<ChannelData>,
    val members: List<ThreadUserData>
) {
    companion object {
        fun from(event: ThreadListSync): ThreadListSyncData = with(event.sync) {
            return ThreadListSyncData(
                guildId,
                channelIds,
                threads.map { it.toData() },
                members.map { ThreadUserData.from(it) }
            )
        }
    }
}