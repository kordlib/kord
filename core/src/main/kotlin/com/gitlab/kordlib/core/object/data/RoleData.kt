package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoleData(
        val id: String,
        var name: String? = null,
        var color: Int? = null,
        var hoist: Boolean? = null,
        var position: Int? = null,
        var permissions: Permissions? = null,
        var managed: Boolean? = null,
        var mentionable: Boolean? = null,
        @SerialName("guild_id")
        var guildId: String? = null
) {
    companion object {
        val description get() = description(RoleData::id)

        fun from(entity: Role) = with(entity) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: DeletedGuildRole) = with(entity) { RoleData(id, guildId) }
        fun from(entity: AuditLogRoleChange) = with(entity) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable) }
        fun from(entity: GuildRole) = with(entity.role) { RoleData(id, name, color, hoist, position, permissions, managed, mentionable, entity.guildId) }
    }
}
