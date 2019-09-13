package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Channel
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.gateway.ChannelUpdate
import kotlinx.serialization.Serializable

@Serializable
data class ChannelData(
        val id: String,
        val type: ChannelType,
        val guildId: String? = null,
        val position: Int? = null,
        val permissionOverwrites: List<PermissionOverwriteData> = emptyList(),
        val name: String? = null,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        val lastMessageId: String? = null,
        val bitrate: Int? = null,
        val userLimit: Int? = null,
        val rateLimitPerUser: Int? = null,
        val recipients: List<String>? = null,
        val icon: String? = null,
        val ownerId: String? = null,
        val applicationId: String? = null,
        val parentId: String? = null,
        val lastPinTimestamp: String? = null
) {


    companion object {
        val description get() = description(ChannelData::id)

        fun from(entity: Channel) = with(entity) {
            ChannelData(
                    id,
                    type,
                    guildId,
                    position,
                    permissionOverwrites.orEmpty().map { PermissionOverwriteData.from(it) },
                    name,
                    topic,
                    nsfw,
                    lastMessageId,
                    bitrate,
                    userLimit,
                    rateLimitPerUser,
                    recipients?.map { it.id },
                    icon,
                    ownerId,
                    applicationId,
                    parentId,
                    lastPinTimestamp
            )
        }
    }

}