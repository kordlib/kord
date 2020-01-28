package com.gitlab.kordlib.rest.builder.channel

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.rest.builder.AuditRequestBuilder
import com.gitlab.kordlib.rest.builder.KordDsl
import com.gitlab.kordlib.rest.json.request.ChannelModifyPatchRequest

@KordDsl
class CategoryModifyBuilder: AuditRequestBuilder<ChannelModifyPatchRequest> {
    override var reason: String? = null
    /**
     * The name of the category.
     */
    var name: String? = null

    /**
     * The position of this category in the guild's channel list.
     */
    var position: Int? = null

    /**
     *  The permission overwrites for this category.
     */
    val permissionOverwrites: MutableSet<Overwrite> = mutableSetOf()


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

    override fun toRequest(): ChannelModifyPatchRequest = ChannelModifyPatchRequest(
            name = name,
            position = position,
            permissionOverwrites = permissionOverwrites.toList()
    )
}