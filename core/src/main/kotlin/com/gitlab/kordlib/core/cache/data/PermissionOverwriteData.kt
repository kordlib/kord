package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.*
import kotlinx.serialization.Serializable

@Serializable
data class PermissionOverwriteData(
        val id: Snowflake,
        val type: OverwriteType,
        val allowed: Permissions,
        val denied: Permissions
) {
    companion object {
        fun from(entity: Overwrite) = with(entity) {
            PermissionOverwriteData(id, type, allow, deny)
        }
    }
}