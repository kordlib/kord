package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.cache.api.data.description
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.DiscordChannel
import kotlinx.serialization.Serializable

@Serializable
@KordUnstableApi
data class ChannelData(
        val id: Long,
        val type: ChannelType,
        val guildId: Long? = null,
        val position: Int? = null,
        val permissionOverwrites: List<PermissionOverwriteData> = emptyList(),
        val name: String? = null,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        val lastMessageId: Long? = null,
        val bitrate: Int? = null,
        val userLimit: Int? = null,
        val rateLimitPerUser: Int? = null,
        val recipients: List<Long>? = null,
        val icon: String? = null,
        val ownerId: Long? = null,
        val applicationId: Long? = null,
        val parentId: Long? = null,
        val lastPinTimestamp: String? = null
) {


    companion object {
        val description get() = description(ChannelData::id)

        fun from(entity: DiscordChannel) = with(entity) {
            ChannelData(
                    id.toLong(),
                    type,
                    guildId?.toLong(),
                    position,
                    permissionOverwrites.orEmpty().map { PermissionOverwriteData.from(it) },
                    name,
                    topic,
                    nsfw,
                    lastMessageId?.toLong(),
                    bitrate,
                    userLimit,
                    rateLimitPerUser,
                    recipients?.map { it.id.toLong() },
                    icon,
                    ownerId?.toLong(),
                    applicationId?.toLong(),
                    parentId?.toLong(),
                    lastPinTimestamp
            )
        }
    }

}

@OptIn(KordUnstableApi::class)
fun DiscordChannel.toData() = ChannelData.from(this)