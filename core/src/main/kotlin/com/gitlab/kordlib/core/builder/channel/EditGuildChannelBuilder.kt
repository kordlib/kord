package com.gitlab.kordlib.core.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.core.builder.AuditRequestBuilder
import com.gitlab.kordlib.core.entity.PermissionOverwrite
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.ChannelModifyPatchRequest

class TextChannelModifyBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    var rateLimitPerUser: Int? = null
    val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            rateLimitPerUser = rateLimitPerUser,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.value, it.type.toString(), it.allowed.code, it.denied.code) },
            parentId = parentId?.value
    )

}

class UpdateVoiceChannelBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var parentId: Snowflake? = null
    var bitrate: Int? = null
    var userLimit: Int? = null
    val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            parentId = parentId?.value,
            bitrate = bitrate,
            userLimit = userLimit,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.value, it.type.toString(), it.allowed.code, it.denied.code) }
    )
}

class UpdateNewsChannelBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    var topic: String? = null
    var nsfw: Boolean? = null
    var parentId: Snowflake? = null
    val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.value, it.type.toString(), it.allowed.code, it.denied.code) },
            parentId = parentId?.value
    )
}

class UpdateStoreChannelBuilder : AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    var name: String? = null
    var position: Int? = null
    val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.value, it.type.toString(), it.allowed.code, it.denied.code) }
    )
}