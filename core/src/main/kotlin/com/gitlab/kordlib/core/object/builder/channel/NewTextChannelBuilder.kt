package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.CreateGuildChannelRequest

class NewTextChannelBuilder(
        var name: String? = null,
        var topic: String? = null,
        var rateLimitPerUser: Int? = null,
        var position: Int? = null,
        var parentId: Snowflake? = null,
        var nsfw: Boolean? = null,
        val permissionOverwrites: MutableList<PermissionOverwrite> = mutableListOf()
) {
    internal fun toRequest(): CreateGuildChannelRequest = CreateGuildChannelRequest(
            name = name!!,
            topic = topic,
            rateLimitPerUser = rateLimitPerUser,
            position = position,
            parentId = parentId?.value,
            nsfw = nsfw,
            permissionOverwrite = permissionOverwrites.map { it.toOverwrite() },
            type = ChannelType.GuildText
    )
}