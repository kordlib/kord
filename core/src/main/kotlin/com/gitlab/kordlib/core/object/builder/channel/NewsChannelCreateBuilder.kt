package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.`object`.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.`object`.builder.RequestBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

class NewsChannelCreateBuilder: AuditRequestBuilder<GuildCreateChannelRequest> {
    override var reason: String? = null
    lateinit var name: String
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    var position: Int? = null
    val permissionOverwrites: MutableList<PermissionOverwrite> = mutableListOf()

    override fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name,
            topic = topic,
            nsfw = nsfw,
            parentId = parentId?.value,
            position = position,
            permissionOverwrite = permissionOverwrites.map { it.toOverwrite() }
    )
}