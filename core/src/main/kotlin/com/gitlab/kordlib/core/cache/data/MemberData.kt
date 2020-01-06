package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordAddedGuildMember
import com.gitlab.kordlib.common.entity.DiscordGuildMember
import com.gitlab.kordlib.common.entity.DiscordPartialGuildMember
import com.gitlab.kordlib.common.entity.DiscordUpdatedGuildMember
import kotlinx.serialization.Serializable

private val MemberData.id get() = "$userId$guildId"

@Serializable
data class MemberData(
        val userId: Long,
        val guildId: Long,
        val nick: String? = null,
        val roles: List<String>,
        val joinedAt: String,
        val premiumSince: String?
) {
    operator fun plus(update: DiscordUpdatedGuildMember) =
            copy(nick = update.nick, roles = update.roles, premiumSince = update.premiumSince)

    companion object {
        val description get() = description(MemberData::id)

        fun from(userId: String, guildId: String, entity: DiscordGuildMember) =
                with(entity) { MemberData(userId.toLong(), guildId.toLong(), nick, roles, joinedAt, premiumSince) }

        fun from(userId: String, entity: DiscordAddedGuildMember) =
                with(entity) { MemberData(userId.toLong(), guildId.toLong(), nick, roles, joinedAt, premiumSince) }

        fun from(userId: String, guildId: String, entity: DiscordPartialGuildMember) =
                with(entity) { MemberData(userId.toLong(), guildId.toLong(), nick, roles, joinedAt, premiumSince) }

    }
}