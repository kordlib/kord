package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Emoji
import com.gitlab.kordlib.common.entity.PartialEmoji
import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EmojiData(
        val id: String,
        var name: String,
        var roles: List<String>? = null,
        var user: User? = null,
        @SerialName("require_colons")
        var requireColons: Boolean? = null,
        var managed: Boolean? = null,
        var animated: Boolean? = null
) {
    companion object {
        val description get() = description(EmojiData::id)

        fun from(id: String, entity: Emoji) =
                with(entity) { EmojiData(id, name, roles, user, requireColons, managed, animated) }

        fun from(entity: PartialEmoji) =
                with(entity) { EmojiData(id, name) }

    }
}
