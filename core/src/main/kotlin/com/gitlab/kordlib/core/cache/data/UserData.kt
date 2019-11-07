package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
        val id: Long,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null
) {
    companion object {

        val description
            get() = description(UserData::id) {
                link(UserData::id to MemberData::userId)
                link(UserData::id to WebhookData::userid)
                link(UserData::id to VoiceStateData::userId)
                link(UserData::id to PresenceData::userId)
            }

        fun from(entity: User) = with(entity) {
            UserData(
                    id.toLong(),
                    username,
                    discriminator,
                    avatar,
                    bot
            )
        }

    }
}
