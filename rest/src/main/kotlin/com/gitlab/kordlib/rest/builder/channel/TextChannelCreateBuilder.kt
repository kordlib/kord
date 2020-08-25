package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

@KordDsl
class TextChannelCreateBuilder : AuditRequestBuilder<@OptIn(KordUnstableApi::class) GuildCreateChannelRequest> {
    override var reason: String? = null
    lateinit var name: String
    var topic: String? = null
    var rateLimitPerUser: Int? = null
    var position: Int? = null
    var parentId: Snowflake? = null
    var nsfw: Boolean? = null
    @OptIn(KordUnstableApi::class)
    val permissionOverwrites: MutableList<Overwrite> = mutableListOf()

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name,
            topic = topic,
            rateLimitPerUser = rateLimitPerUser,
            position = position,
            parentId = parentId?.value,
            nsfw = nsfw,
            permissionOverwrite = permissionOverwrites,
            type = ChannelType.GuildText
    )
}