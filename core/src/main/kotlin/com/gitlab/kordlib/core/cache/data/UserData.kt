package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
        val id: String,
        var username: String,
        var discriminator: String,
        var avatar: String? = null,
        var bot: Boolean? = null
) {
    companion object {

        val description
            get() = description(UserData::id) {
                link(UserData::id to MemberData::userId)
                link(UserData::id to MessageData::authorId)
                link(UserData::id to WebhookData::userid)
                link(UserData::id to VoiceStateData::userId)
                link(UserData::id to PresenceData::userId)
            }

        fun from(entity: User) = with(entity) {
            UserData(
                    id,
                    username,
                    discriminator,
                    avatar,
                    bot
            )
        }

    }
}
