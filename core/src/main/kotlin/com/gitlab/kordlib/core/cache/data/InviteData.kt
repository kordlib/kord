package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.rest.json.response.InviteResponse
import kotlinx.serialization.Serializable

@Serializable
data class InviteData(
        val code: String,
        val guildId: Long?,
        val channelId: Long,
        val targetUserId: Long?,
        val inviterId: Long?,
        val approximatePresenceCount: Int?,
        val approximateMemberCount: Int?
) {
    companion object {
        fun from(entity: InviteResponse) = with(entity) {
            InviteData(code!!, guild!!.id.toLong(), channel!!.id.toLong(), targetUser?.id?.toLong(), inviter?.id?.toLong(), approximatePresenceCount, approximateMemberCount)
        }
    }
}