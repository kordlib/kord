package dev.kord.rest.json.request

import dev.kord.common.entity.Overwrite
import dev.kord.common.entity.OverwriteType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
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
    val name: Optional<String> = Optional.Missing(),
    val position: OptionalInt? = OptionalInt.Missing,
    val topic: Optional<String?> = Optional.Missing(),
    val nsfw: OptionalBoolean? = OptionalBoolean.Missing,
    @SerialName("rate_limit_per_user")
    val rateLimitPerUser: OptionalInt? = OptionalInt.Missing,
    val bitrate: OptionalInt? = OptionalInt.Missing,
    @SerialName("user_limit")
    val userLimit: OptionalInt? = OptionalInt.Missing,
    @SerialName("permission_overwrites")
    val permissionOverwrites: Optional<Set<Overwrite>?> = Optional.Missing(),
    @SerialName("parent_id")
    val parentId: OptionalSnowflake? = OptionalSnowflake.Missing
)

@Serializable
data class ChannelPermissionEditRequest(
    val allow: Permissions,
    val deny: Permissions,
    val type: OverwriteType
)
