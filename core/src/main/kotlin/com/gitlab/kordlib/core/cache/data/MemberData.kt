package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.AddedGuildMember
import com.gitlab.kordlib.common.entity.GuildMember
import com.gitlab.kordlib.common.entity.PartialGuildMember
import kotlinx.serialization.Serializable

private val MemberData.id get() = "$userId$guildId"

@Serializable
data class MemberData(
        val userId: Long,
        val guildId: Long,
        val nick: String? = null,
        val roles: List<String>,
        val joinedAt: String
) {
    companion object {
        val description get() = description(MemberData::id)

        fun from(userId: String, guildId: String, entity: GuildMember) =
                with(entity) { MemberData(userId.toLong(), guildId.toLong(), nick, roles, joinedAt) }

        fun from(userId: String, entity: AddedGuildMember) =
                with(entity) { MemberData(userId.toLong(), guildId.toLong(), nick, roles, joinedAt) }

        fun from(userId: String, guildId: String, entity: PartialGuildMember) =
                with(entity) { MemberData(userId.toLong(), guildId.toLong(), nick, roles, joinedAt) }

    }
}