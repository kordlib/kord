package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.mapList
import dev.kord.gateway.ThreadMembersUpdate

class ThreadMembersUpdateEventData(
    val id: Snowflake,
    val guildId: Snowflake,
    val memberCount: Int,
    val addedMembers: Optional<List<ThreadMemberData>> = Optional.Missing(),
    val removedMemberIds: Optional<List<Snowflake>> = Optional.Missing()
) {
    companion object {
        fun from(event: ThreadMembersUpdate) = with(event.members) {
            ThreadMembersUpdateEventData(
                id,
                guildId,
                memberCount,
                addedMembers.mapList { ThreadMemberData.from(it) },
                removedMemberIds
            )
        }
    }
}