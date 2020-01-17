package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

@KordDsl
class NewsChannelCreateBuilder: AuditRequestBuilder<GuildCreateChannelRequest> {
    override var reason: String? = null
    lateinit var name: String
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    var position: Int? = null
    val permissionOverwrites: MutableList<Overwrite> = mutableListOf()

    override fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name,
            topic = topic,
            nsfw = nsfw,
            parentId = parentId?.value,
            position = position,
            permissionOverwrite = permissionOverwrites
    )
}