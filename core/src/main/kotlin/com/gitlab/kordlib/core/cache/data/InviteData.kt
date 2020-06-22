package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.TargetUserType
import com.gitlab.kordlib.rest.json.response.InviteResponse
import kotlinx.serialization.Serializable

@Serializable
data class InviteData(
        val code: String,
        val guild: PartialGuildData?,
        val channelId: Long,
        val targetUserId: Long?,
        val inviterId: Long?,
        val approximatePresenceCount: Int?,
        val approximateMemberCount: Int?,
        val targetUserType: TargetUserType?
) {

    companion object {

        fun from(entity: InviteResponse) = with(entity) {
            InviteData(
                    code,
                    guild?.let { PartialGuildData.from(it) },
                    channel.id.toLong(),
                    targetUser?.id?.toLong(),
                    inviter?.id?.toLong(),
                    approximatePresenceCount,
                    approximateMemberCount,
                    targetUserType
            )
        }
    }
}