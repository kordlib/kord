package dev.kord.core.cache.data

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

@Serializable
public data class PermissionOverwriteData(
    val id: Snowflake,
    val type: OverwriteType,
    val allowed: Permissions,
    val denied: Permissions
) {
    public companion object {
        public fun from(entity: Overwrite): PermissionOverwriteData = with(entity) {
            PermissionOverwriteData(id, type, allow, deny)
        }
    }
}
