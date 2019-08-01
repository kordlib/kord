package com.gitlab.kordlib.core.`object`.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.PatchModifyChannelRequest

abstract class UpdateGuildChannelBuilder {
    internal abstract fun toRequest(): PatchModifyChannelRequest
}

data class UpdateTextChannelBuilder(
        var name: String? = null,
        var position: Int? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        var parentId: Snowflake? = null,
        var rateLimitPerUser: Int? = null,
        val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()
) : UpdateGuildChannelBuilder() {

    override fun toRequest(): PatchModifyChannelRequest = PatchModifyChannelRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            rateLimitPerUser = rateLimitPerUser,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.toString(), it.type, it.allowed.code, it.denied.code) },
            parentId = parentId?.toString()
    )

}

class UpdateVoiceChannelBuilder(
        var name: String? = null,
        var position: Int? = null,
        var parentId: Snowflake? = null,
        var bitrate: Int? = null,
        var userLimit: Int? = null,
        val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()
) : UpdateGuildChannelBuilder() {
    override fun toRequest(): PatchModifyChannelRequest = PatchModifyChannelRequest(
            name = name,
            position = position,
            parentId = parentId?.toString(),
            bitrate = bitrate,
            userLimit = userLimit,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.toString(), it.type, it.allowed.code, it.denied.code) }
    )
}

class UpdateNewsChannelBuilder(
        var name: String? = null,
        var position: Int? = null,
        var topic: String? = null,
        var nsfw: Boolean? = null,
        var parentId: Snowflake? = null,
        val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()
) : UpdateGuildChannelBuilder() {
    override fun toRequest(): PatchModifyChannelRequest = PatchModifyChannelRequest(
            name = name,
            position = position,
            topic = topic,
            nsfw = nsfw,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.toString(), it.type, it.allowed.code, it.denied.code) },
            parentId = parentId?.toString()
    )
}

class UpdateStoreChannelBuilder(
        var name: String? = null,
        var position: Int? = null,
        val permissionOverwrites: MutableSet<PermissionOverwrite> = mutableSetOf()
) : UpdateGuildChannelBuilder() {
    override fun toRequest(): PatchModifyChannelRequest = PatchModifyChannelRequest(
            name = name,
            position = position,
            permissionOverwrites = permissionOverwrites.map { Overwrite(it.target.toString(), it.type, it.allowed.code, it.denied.code) }
    )
}