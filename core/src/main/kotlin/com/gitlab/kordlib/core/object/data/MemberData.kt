package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.AddedGuildMember
import com.gitlab.kordlib.common.entity.GuildMember
import com.gitlab.kordlib.common.entity.PartialGuildMember
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val GuildMemberData.id get() = "$userId$guildId"

@Serializable
data class GuildMemberData(
        var userId: String,
        @SerialName("guild_id")
        var guildId: String,
        var nick: String? = null,
        var roles: List<String>,
        @SerialName("joined_at")
        var joinedAt: String,
        var deaf: Boolean,
        var mute: Boolean
) {
    companion object {
        val description get() = description(GuildMemberData::id)

        fun from(userId: String, guildId: String, entity: GuildMember) =
                with(entity) { GuildMemberData(userId, guildId, nick, roles, joinedAt, deaf, mute) }

        fun from(userId: String, entity: AddedGuildMember) =
                with(entity) { GuildMemberData(userId, guildId, nick, roles, joinedAt, deaf, mute) }

        fun from(userId: String, guildId: String, entity: PartialGuildMember) =
                with(entity) { GuildMemberData(userId, guildId, nick, roles, joinedAt, deaf, mute) }

    }
}