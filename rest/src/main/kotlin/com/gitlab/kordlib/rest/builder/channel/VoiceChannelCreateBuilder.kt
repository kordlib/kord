package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

@KordDsl
class VoiceChannelCreateBuilder : AuditRequestBuilder<GuildCreateChannelRequest> {
    override var reason: String? = null
    lateinit var name: String
    var bitrate: Int? = null
    var userLimit: Int? = null
    var parentId: Snowflake? = null
    var position: Int? = null
    val permissionOverwrites: MutableList<Overwrite> = mutableListOf()

    override fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name,
            bitrate = bitrate,
            userLimit = userLimit,
            parentId = parentId?.asString,
            position = position,
            permissionOverwrite = permissionOverwrites,
            type = ChannelType.GuildVoice
    )
}