package dev.kord.core.cache.data

import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordGuildRole
import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.map
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
    val id: Snowflake,
    val guildId: Snowflake,
    val name: String,
    val color: Int,
    val hoisted: Boolean,
    val position: Int,
    val permissions: Permissions,
    val managed: Boolean,
    val mentionable: Boolean,
    val tags: Optional<RoleTagsData> = Optional.Missing()
) {
    companion object {
        val description = description(RoleData::id)

        fun from(guildId: Snowflake, entity: DiscordRole) = with(entity) {
            RoleData(
                id,
                guildId,
                name,
                color,
                hoist,
                position,
                permissions,
                managed,
                mentionable,
                tags.map { RoleTagsData.from(it) })
        }

        fun from(entity: DiscordGuildRole) = from(entity.guildId, entity.role)

    }
}

fun DiscordRole.toData(guildId: Snowflake): RoleData {
    return RoleData.from(DiscordGuildRole(guildId, this))
}
