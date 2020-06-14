package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.DiscordUser
import com.gitlab.kordlib.common.entity.Premium
import com.gitlab.kordlib.common.entity.UserFlags
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
        val id: Long,
        val username: String,
        val discriminator: String,
        val avatar: String? = null,
        val bot: Boolean? = null,
        val flags: UserFlags? = null,
        val premium: Premium? = null
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
                    flags,
                    premiumType
            )
        }

    }
}

fun DiscordUser.toData() = UserData.from(this)