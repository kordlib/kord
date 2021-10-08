package dev.kord.core.cache.data

import dev.kord.cache.api.data.DataDescription
import dev.kord.cache.api.data.description
import dev.kord.common.entity.DiscordGuildRole
import dev.kord.common.entity.DiscordRole
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.map
import kotlinx.serialization.Serializable

@Serializable
public data class RoleData(
    val id: Snowflake,
    val guildId: Snowflake,
    val name: String,
    val color: Int,
    val hoisted: Boolean,
    val icon: Optional<String?>,
    val unicodeEmoji: Optional<String?>,
    val position: Int,
    val permissions: Permissions,
    val managed: Boolean,
    val mentionable: Boolean,
    val tags: Optional<RoleTagsData> = Optional.Missing()
) {
    public companion object {
        public val description: DataDescription<RoleData, Snowflake> = description(RoleData::id)

        public fun from(guildId: Snowflake, entity: DiscordRole): RoleData = with(entity) {
            RoleData(
                id,
                guildId,
                name,
                color,
                hoist,
                icon,
                unicodeEmoji,
                position,
                permissions,
                managed,
                mentionable,
                tags.map { RoleTagsData.from(it) })
        }

        public fun from(entity: DiscordGuildRole): RoleData = from(entity.guildId, entity.role)

    }
}

public fun DiscordRole.toData(guildId: Snowflake): RoleData {
    return RoleData.from(DiscordGuildRole(guildId, this))
}
