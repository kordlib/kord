package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.Overwrite
import com.gitlab.hopebaron.common.entity.Permission
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PutModifyChannelRequest(val name: String,
                                   val position: Int,
                                   val topic: String,
                                   val nsfw: Boolean,
                                   @SerialName("rate_limit_per_user")
                                   val rateLimitPerUser: Int,
                                   val bitrate: Int,
                                   @SerialName("user_limit")
                                   val userLimit: Int,
                                   @SerialName("permission_overwrites")
                                   val permissionOverwrites: List<Overwrite>,
                                   @SerialName("parent_id")
                                   val parentId: String)

@Serializable
data class PatchModifyChannelRequest(val name: String? = null,
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
                                     val parentId: String? = null)

@Serializable
data class EditChannelPermissionRequest(val allow: Permission, val deny: Permission, val type: String)
