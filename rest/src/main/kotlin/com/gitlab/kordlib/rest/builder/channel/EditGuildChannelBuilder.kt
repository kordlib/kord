package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.rest.json.request.ChannelModifyPatchRequest

@KordDsl
class TextChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    var rateLimitPerUser: Int? = null
    var permissionOverwrites: Set<Overwrite>? = null

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            rateLimitPerUser = rateLimitPerUser,
            permissionOverwrites = permissionOverwrites?.toList(),
            parentId = parentId?.asString
    )

}

@KordDsl
class VoiceChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var parentId: Snowflake? = null
    var bitrate: Int? = null
    var userLimit: Int? = null
    var permissionOverwrites: Set<Overwrite>? = null

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            parentId = parentId?.asString,
            bitrate = bitrate,
            userLimit = userLimit,
            permissionOverwrites = permissionOverwrites?.toList()
    )
}

@KordDsl
class NewsChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    var permissionOverwrites: Set<Overwrite>? = null

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            permissionOverwrites = permissionOverwrites?.toList(),
            parentId = parentId?.asString
    )
}

@KordDsl
class StoreChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    val permissionOverwrites: MutableSet<Overwrite>? = null

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            permissionOverwrites = permissionOverwrites?.toList()
    )
}