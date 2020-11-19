package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.DiscordBitSet
import com.gitlab.kordlib.common.entity.Overwrite
import kotlinx.serialization.Serializable

@Serializable
data class PermissionOverwriteData(
        val id: Long,
        val type: String,
        val allowed: DiscordBitSet,
        val denied: DiscordBitSet
) {
    companion object {
        fun from(entity: Overwrite) = with(entity) {
            PermissionOverwriteData(id.toLong(), type, allow, deny)
        }
    }
}