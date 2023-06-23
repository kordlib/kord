package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.gateway.ThreadListSync

public class ThreadListSyncData(
    public val guildId: Snowflake,
    public val channelIds: Optional<List<Snowflake>> = Optional.Missing(),
    public val threads: List<ChannelData>,
    public val members: List<ThreadMemberData>
) {
    public companion object {
        public fun from(event: ThreadListSync): ThreadListSyncData = with(event.sync) {
            return ThreadListSyncData(
                guildId,
                channelIds,
                threads.map { it.toData() },
                members.map { ThreadMemberData.from(it) }
            )
        }
    }
}
