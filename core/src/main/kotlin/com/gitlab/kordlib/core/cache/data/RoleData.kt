package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordGuildRole
import com.gitlab.kordlib.common.entity.DiscordRole
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Snowflake
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
        val mentionable: Boolean
) {
    companion object {
        val description = description(RoleData::id)

        fun from(guildId: Snowflake, entity: DiscordRole) = with(entity) {
            RoleData(id, guildId, name, color, hoist, position, permissions, managed, mentionable)
        }

        fun from(entity: DiscordGuildRole) = from(entity.guildId, entity.role)

    }
}
