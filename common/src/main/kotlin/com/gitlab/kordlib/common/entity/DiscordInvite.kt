package com.gitlab.kordlib.common.entity

import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalInt
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordInvite(
        val code: String,
        val guild: Optional<DiscordPartialGuild> = Optional.Missing(),
        val channel: DiscordChannel,
        val inviter: Optional<DiscordUser> = Optional.Missing(),
        @SerialName("target_user")
        val targetUser: Optional<DiscordUser> = Optional.Missing(),
        @SerialName("target_user_type")
        val targetUserType: Optional<TargetUserType> = Optional.Missing(),
        @SerialName("approximate_presence_count")
        val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
        @SerialName("approximate_member_count")
        val approximateMemberCount: OptionalInt = OptionalInt.Missing,
)

@Serializable
data class DiscordPartialInvite(
        /*
        Do not trust the docs:
        2020-11-06 This is actually never null, the endpoint(Get Guild Vanity URL) returns
        a HTTP 4xx instead when the guild has no vanity url.
         */
        val code: String?,
        val uses: Int
)

@Serializable
data class DiscordInviteMetadata(
        val uses: Int,
        @SerialName("max_uses")
        val maxUses: Int,
        @SerialName("max_age")
        val maxAge: Int,
        val temporary: Boolean,
        @SerialName("created_at")
        val createdAt: String,
)