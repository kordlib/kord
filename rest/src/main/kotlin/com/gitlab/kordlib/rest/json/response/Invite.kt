package com.gitlab.kordlib.rest.json.response

import com.gitlab.kordlib.common.entity.DiscordChannel
import com.gitlab.kordlib.common.entity.DiscordPartialGuild
import com.gitlab.kordlib.common.entity.DiscordUser
import com.gitlab.kordlib.common.entity.TargetUserType
import kotlinx.serialization.*
import kotlinx.serialization.internal.IntDescriptor

@Serializable
data class InviteResponse(
        val code: String? = null,
        val guild: DiscordPartialGuild? = null,
        val channel: DiscordChannel? = null,
        val inviter: DiscordUser? = null,
        @SerialName("target_user")
        val targetUser: DiscordUser? = null,
        @SerialName("target_user_type")
        val targetUserType: TargetUserType? = null,
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: Int? = null,
        @SerialName("approximate_member_count")
        val approximateMemberCount: Int? = null,
        val uses: Int? = null
)


@Serializable
data class InviteMetaDataResponse(
        val inviter: DiscordUser,
        val uses: Int,
        @SerialName("max_uses")
        val maxUses: Int,
        @SerialName("max_age")
        val maxAge: Int,
        val temporary: Boolean,
        @SerialName("created_at")
        val createdAt: String
)

@Serializable
data class PartialInvite(val code: String? = null)