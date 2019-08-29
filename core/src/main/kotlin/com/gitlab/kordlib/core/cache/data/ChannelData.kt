package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Channel
import com.gitlab.kordlib.common.entity.ChannelType
import kotlinx.serialization.Serializable

@Serializable
data class ChannelData(
        val id: String,
        var type: ChannelType,
        var guildId: String? = null,
        var position: Int? = null,
        var permissionOverwrites: List<PermissionOverwriteData> = emptyList(),
        var name: String? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        var lastMessageId: String? = null,
        var bitrate: Int? = null,
        var userLimit: Int? = null,
        var rateLimitPerUser: Int? = null,
        var recipients: List<String>? = null,
        var icon: String? = null,
        var ownerId: String? = null,
        var applicationId: String? = null,
        var parentId: String? = null,
        var lastPinTimestamp: String? = null
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