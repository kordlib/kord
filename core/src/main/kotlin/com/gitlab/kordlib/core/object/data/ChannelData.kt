package com.gitlab.kordlib.core.`object`.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.entity.Channel
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Overwrite
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelData(
        val id: String,
        var type: ChannelType,
        @SerialName("guild_id")
        var guildId: String? = null,
        var position: Int? = null,
        @SerialName("permission_overwrites")
        var permissionOverwrites: List<Overwrite>? = null,
        var name: String? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        @SerialName("last_message_id")
        var lastMessageId: String? = null,
        var bitrate: Int? = null,
        @SerialName("user_limit")
        var userLimit: Int? = null,
        @SerialName("rate_limit_per_user")
        var rateLimitPerUser: Int? = null,
        var recipients: List<String>? = null,
        var icon: String? = null,
        @SerialName("owner_id")
        var ownerId: String? = null,
        @SerialName("application_id")
        var applicationId: String? = null,
        @SerialName("parent_id")
        var parentId: String? = null,
        @SerialName("last_pin_timestamp")
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
                    permissionOverwrites,
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