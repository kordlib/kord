package com.gitlab.hopebaron.rest.json.request

import com.gitlab.hopebaron.common.entity.Overwrite
import com.gitlab.hopebaron.common.entity.Snowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PutModifyMessageRequest(val name: String,
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
                                   val parentId: Snowflake)

@Serializable
data class PatchModifyMessageRequest(val name: String? = null,
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
                                     val parentId: Snowflake? = null)
