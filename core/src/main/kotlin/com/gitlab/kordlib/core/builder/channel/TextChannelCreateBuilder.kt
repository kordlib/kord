package com.gitlab.kordlib.core.builder.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.builder.KordDsl
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

@KordDsl
class TextChannelCreateBuilder : AuditRequestBuilder<GuildCreateChannelRequest> {
    override var reason: String? = null
    lateinit var name: String
    var topic: String? = null
    var rateLimitPerUser: Int? = null
    var position: Int? = null
    var parentId: Snowflake? = null
    var nsfw: Boolean? = null
    val permissionOverwrites: MutableList<PermissionOverwrite> = mutableListOf()

    override fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name,
            topic = topic,
            rateLimitPerUser = rateLimitPerUser,
            position = position,
            parentId = parentId?.value,
            nsfw = nsfw,
            permissionOverwrite = permissionOverwrites.map { it.toOverwrite() },
            type = ChannelType.GuildText
    )
}