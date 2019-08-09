package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.Overwrite
import com.gitlab.kordlib.common.entity.Permissions
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChannelModifyPutRequest(
        val name: String,
        val position: Int,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int? = null,
        val bitrate: Int? = null,
        @SerialName("user_limit")
        val userLimit: Int? = null,
        @SerialName("permission_overwrites")
        val permissionOverwrites: List<Overwrite>,
        @SerialName("parent_id")
        val parentId: String? = null
)

@Serializable
data class ChannelModifyPatchRequest(
        val name: String? = null,
        val position: Int? = null,
        val topic: String? = null,
        val nsfw: Boolean? = null,
        @SerialName("rate_limit_per_user")
        val rateLimitPerUser: Int? = null,
        val bitrate: Int? = null,
        @SerialName("user_limit")
        val userLimit: Int? = null,
        @SerialName("permission_overwrites")
        val permissionOverwrites: List<Overwrite>? = null,
        @SerialName("parent_id")
        val parentId: String? = null
)

@Serializable
data class ChannelPermissionEditPutRequest(val allow: Permissions, val deny: Permissions, val type: String)
