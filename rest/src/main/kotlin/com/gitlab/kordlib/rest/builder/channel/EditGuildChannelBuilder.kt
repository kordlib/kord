package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.common.annotation.KordDsl
import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.json.request.ChannelModifyPatchRequest

@KordDsl
class TextChannelModifyBuilder : AuditRequestBuilder<@OptIn(KordUnstableApi::class) ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    var rateLimitPerUser: Int? = null
    @OptIn(KordUnstableApi::class)
    var permissionOverwrites: Set<Overwrite>? = null

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            rateLimitPerUser = rateLimitPerUser,
            permissionOverwrites = permissionOverwrites?.toList(),
            parentId = parentId?.value
    )

}

@KordDsl
class VoiceChannelModifyBuilder : AuditRequestBuilder<@OptIn(KordUnstableApi::class) ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var parentId: Snowflake? = null
    var bitrate: Int? = null
    var userLimit: Int? = null
    @OptIn(KordUnstableApi::class)
    var permissionOverwrites: Set<Overwrite>? = null

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            parentId = parentId?.value,
            bitrate = bitrate,
            userLimit = userLimit,
            permissionOverwrites = permissionOverwrites?.toList()
    )
}

@KordDsl
class NewsChannelModifyBuilder : AuditRequestBuilder<@OptIn(KordUnstableApi::class) ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    @OptIn(KordUnstableApi::class)
    var permissionOverwrites: Set<Overwrite>? = null

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            permissionOverwrites = permissionOverwrites?.toList(),
            parentId = parentId?.value
    )
}

@KordDsl
class StoreChannelModifyBuilder : AuditRequestBuilder<@OptIn(KordUnstableApi::class) ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    @OptIn(KordUnstableApi::class)
    val permissionOverwrites: MutableSet<Overwrite>? = null

    @OptIn(KordUnstableApi::class)
    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            permissionOverwrites = permissionOverwrites?.toList()
    )
}