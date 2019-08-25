package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Permissions
import com.gitlab.kordlib.common.entity.Role
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
        val id: String,
        var guildId: String,
        var name: String,
        var color: Int,
        var hoisted: Boolean,
        var position: Int,
        var permissions: Permissions,
        var managed: Boolean,
        var mentionable: Boolean
) {
    companion object {
        val description get() = description(RoleData::id)
        fun from(guildId: String, entity: Role) = with(entity) { RoleData(id, guildId, name, color, hoist, position, permissions, managed, mentionable) }
    }
}
