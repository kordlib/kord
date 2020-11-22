package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.map
import com.gitlab.kordlib.common.entity.optional.mapList
import com.gitlab.kordlib.gateway.GuildMembersChunkData
import kotlinx.serialization.Serializable

@Serializable
data class MembersChunkData(
        val guildId: Snowflake,
        val members: Set<MemberData>,
        val users: Set<UserData>,
        val chunkIndex: Int,
        val chunkCount: Int,
        val notFound: Optional<Set<Snowflake>> = Optional.Missing(),
        val presences: Optional<List<PresenceData>> = Optional.Missing(),
        val nonce: Optional<String> = Optional.Missing(),
) {
    companion object {

        fun from(entity: GuildMembersChunkData): MembersChunkData = with(entity) {
            MembersChunkData(
                    guildId,
                    members.map { MemberData.from(userId = it.user.value!!.id, guildId = guildId, it) }.toSet(),
                    members.map { UserData.from(it.user.value!!) }.toSet(),
                    chunkIndex,
                    chunkCount,
                    notFound,
                    presences.mapList { PresenceData.from(guildId, it) },
                    nonce
            )
        }

    }
}
