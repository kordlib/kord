package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.ChannelType
import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.GuildCreateChannelRequest

@KordDsl
class CategoryCreateBuilder : AuditRequestBuilder<GuildCreateChannelRequest> {
    override var reason: String? = null
    lateinit var name: String
    var position: Int? = null
    var nsfw: Boolean? = null
    val permissionOverwrites: MutableList<Overwrite> = mutableListOf()

    /**
     * adds a [Overwrite] for the [memberId].
     */
    inline fun addMemberOverwrite(memberId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit){
        permissionOverwrites += PermissionOverwriteBuilder("member", memberId).apply(builder).toOverwrite()
    }

    /**
     * adds a [Overwrite] for the [roleId].
     */
    inline fun addRoleOverwrite(roleId: Snowflake, builder: PermissionOverwriteBuilder.() -> Unit){
        permissionOverwrites += PermissionOverwriteBuilder("role", roleId).apply(builder).toOverwrite()
    }

    override fun toRequest(): GuildCreateChannelRequest = GuildCreateChannelRequest(
            name = name,
            position = position,
            nsfw = nsfw,
            permissionOverwrite = permissionOverwrites,
            type = ChannelType.GuildCategory
    )
}
