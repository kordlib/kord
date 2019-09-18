package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.GuildRole
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Role
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
        val id: Long,
        val guildId: Long,
        val name: String,
        val color: Int,
        val hoisted: Boolean,
        val position: Int,
        val permissions: Permissions,
        val managed: Boolean,
        val mentionable: Boolean
) {
    companion object {
        val description get() = description(RoleData::id)
        fun from(guildId: String, entity: Role) = with(entity) { RoleData(id.toLong(), guildId.toLong(), name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: GuildRole) = from(entity.guildId, entity.role)

    }
}
