package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordEmoji
import kotlinx.serialization.Serializable

@Serializable
data class EmojiData(
        val id: Long,
        val name: String? = null,
        val user: UserData?,
        val roles: List<Long>,
        val requireColons: Boolean,
        val managed: Boolean,
        val animated: Boolean
) {
    companion object {
        val description get() = description(EmojiData::id)

        fun from(id: String, entity: DiscordEmoji) =
                with(entity) {
                    EmojiData(
                            id.toLong(),
                            name,
                            user?.let { UserData.from(it) },
                            roles.orEmpty().map { it.toLong() },
                            requireColons ?: false,
                            managed ?: false,
                            animated ?: false
                    )
                }
    }
}
