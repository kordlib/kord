package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Emoji
import kotlinx.serialization.Serializable

@Serializable
data class EmojiData(
        val id: String,
        val name: String,
        val user: UserData?,
        val roles: List<String>,
        val requireColons: Boolean,
        val managed: Boolean,
        val animated: Boolean
) {
    companion object {
        val description get() = description(EmojiData::id)

        fun from(id: String, entity: Emoji) =
                with(entity) {
                    EmojiData(
                            id,
                            name,
                            user?.let { UserData.from(it) },
                            roles ?: emptyList(),
                            requireColons ?: false,
                            managed ?: false,
                            animated ?: false
                    )
                }
    }
}
