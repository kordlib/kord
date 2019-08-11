package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

class NewVoiceChannelBuilder (
        var bitrate: Int? = null,
        var name: String? = null,
        var userLimit: Int? = null,
        var parentId: Snowflake? = null,
        var position: Int? = null,
        val permissionOverwrites: MutableList<PermissionOverwrite> = mutableListOf()
) {
    fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name!!,
            bitrate = bitrate,
            userLimit = userLimit,
            parentId = parentId?.value,
            position = position,
            permissionOverwrite = permissionOverwrites.map { it.toOverwrite() },
            type = ChannelType.GuildVoice
    )
}