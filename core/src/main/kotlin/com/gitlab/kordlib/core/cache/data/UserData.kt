package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordUser
import com.gitlab.kordlib.common.entity.UserFlag
import com.gitlab.kordlib.common.entity.UserFlags
import com.gitlab.kordlib.gateway.DiscordInviteUser
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
        val id: Long,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null,
        val flags: UserFlags? = null
) {
    companion object {

        val description
            get() = description(UserData::id) {
                link(UserData::id to MemberData::userId)
                link(UserData::id to WebhookData::userid)
                link(UserData::id to VoiceStateData::userId)
                link(UserData::id to PresenceData::userId)
            }

        fun from(entity: DiscordUser) = with(entity) {
            UserData(
                    id.toLong(),
                    username,
                    discriminator,
                    avatar,
                    bot,
                    flags
            )
        }

    }
}
