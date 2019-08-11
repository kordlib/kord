package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

class NewNewsChannelBuilder (
        var name: String? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        var parentId: Snowflake? = null,
        var position: Int? = null,
        val permissionOverwrites: MutableList<PermissionOverwrite> = mutableListOf()
) {
    fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name!!,
            topic = topic,
            nsfw = nsfw,
            parentId = parentId?.value,
            position = position,
            permissionOverwrite = permissionOverwrites.map { it.toOverwrite() }
    )
}