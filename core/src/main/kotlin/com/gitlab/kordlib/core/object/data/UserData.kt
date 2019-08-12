package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Premium
import com.gitlab.kordlib.common.entity.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
        val id: String,
        var username: String,
        var discriminator: String,
        var avatar: String? = null,
        var bot: Boolean? = null,
        @SerialName("mfa_enable")
        var mfaEnable: Boolean? = null,
        var locale: String? = null,
        var flags: Int? = null,
        @SerialName("premium_type")
        var premiumType: Premium? = null,
        var verified: Boolean? = null,
        var email: String? = null
) {
    companion object {

        val description
            get() = description(UserData::id) {
                link(UserData::id to GuildMemberData::userId)
                link(UserData::id to MessageData::authorId)
                link(UserData::id to WebhookData::userid)
            }

        fun from(entity: User) = with(entity) {
            UserData(
                    id,
                    username,
                    discriminator,
                    avatar,
                    bot,
                    mfaEnable,
                    locale,
                    flags,
                    premiumType,
                    verified,
                    email
            )
        }

    }
}
