package dev.kord.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.optional
import kotlinx.serialization.Serializable

private val MemberData.id get() = "$userId$guildId"

@Serializable
data class MemberData(
        val userId: Snowflake,
        val guildId: Snowflake,
        val nick: Optional<String?> = Optional.Missing(),
        val roles: List<Snowflake>,
        val joinedAt: String,
        val premiumSince: Optional<String?>,
        val pending: OptionalBoolean = OptionalBoolean.Missing
) {

    companion object {
        val description = description(MemberData::id)

        fun from(userId: Snowflake, guildId: Snowflake, entity: DiscordGuildMember) = with(entity) {
            MemberData(userId = userId, guildId = guildId, nick, roles, joinedAt, premiumSince, pending)
        }

        fun from(userId: Snowflake, entity: DiscordAddedGuildMember) = with(entity) {
            MemberData(userId = userId, guildId = guildId, nick, roles, joinedAt, premiumSince, pending)
        }

        fun from(entity: DiscordUpdatedGuildMember) = with(entity){
            MemberData(userId = user.id, guildId = guildId, nick, roles, joinedAt, premiumSince, pending)
        }

    }
}

fun DiscordGuildMember.toData(userId: Snowflake, guildId: Snowflake) = MemberData.from(userId, guildId, this)
