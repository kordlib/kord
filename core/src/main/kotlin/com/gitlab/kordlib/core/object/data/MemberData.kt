package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.AddedGuildMember
import com.gitlab.kordlib.common.entity.GuildMember
import com.gitlab.kordlib.common.entity.PartialGuildMember
import kotlinx.serialization.Serializable

private val MemberData.id get() = "$userId$guildId"

@Serializable
data class MemberData(
        var userId: String,
        var guildId: String,
        var nick: String? = null,
        var roles: List<String>,
        var joinedAt: String
) {
    companion object {
        val description get() = description(MemberData::id)

        fun from(userId: String, guildId: String, entity: GuildMember) =
                with(entity) { MemberData(userId, guildId, nick, roles, joinedAt) }

        fun from(userId: String, entity: AddedGuildMember) =
                with(entity) { MemberData(userId, guildId, nick, roles, joinedAt) }

        fun from(userId: String, guildId: String, entity: PartialGuildMember) =
                with(entity) { MemberData(userId, guildId, nick, roles, joinedAt) }

    }
}