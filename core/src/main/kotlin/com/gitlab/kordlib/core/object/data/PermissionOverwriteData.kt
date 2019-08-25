package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.common.entity.Overwrite
import kotlinx.serialization.Serializable

@Serializable
data class PermissionOverwriteData(
        val id: String,
        val type: String,
        val allowed: Int,
        val denied: Int
) {
    companion object {
        fun from(entity: Overwrite) = with(entity) {
            PermissionOverwriteData(id, type, allow, deny)
        }
    }
}