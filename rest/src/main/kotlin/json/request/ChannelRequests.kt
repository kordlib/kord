package dev.kord.rest.json.request

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ChannelModifyPutRequest(
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
public data class ChannelModifyPatchRequest(
    val name: Optional<String> = Optional.Missing(),
    val position: OptionalInt? = OptionalInt.Missing,
    val topic: Optional<String?> = Optional.Missing(),
    val nsfw: OptionalBoolean? = OptionalBoolean.Missing,
    @SerialName("rate_limit_per_user")
    val rateLimitPerUser: Optional<DurationInSeconds?> = Optional.Missing(),
    val bitrate: OptionalInt? = OptionalInt.Missing,
    @SerialName("user_limit")
    val userLimit: OptionalInt? = OptionalInt.Missing,
    @SerialName("permission_overwrites")
    val permissionOverwrites: Optional<Set<Overwrite>?> = Optional.Missing(),
    @SerialName("parent_id")
    val parentId: OptionalSnowflake? = OptionalSnowflake.Missing,
    val archived: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("auto_archive_duration")
    val autoArchiveDuration: Optional<ArchiveDuration> = Optional.Missing(),
    val locked: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("rtc_region")
    val rtcRegion: Optional<String?> = Optional.Missing(),
    val invitable: OptionalBoolean = OptionalBoolean.Missing,
    @SerialName("video_quality_mode")
    val videoQualityMode: Optional<VideoQualityMode?> = Optional.Missing(),
    @SerialName("default_auto_archive_duration")
    val defaultAutoArchiveDuration: Optional<ArchiveDuration?> = Optional.Missing(),
)

@Serializable
public data class ChannelPermissionEditRequest(
    val allow: Permissions,
    val deny: Permissions,
    val type: OverwriteType
)

@Serializable
public data class StartThreadRequest(
    val name: String,
    @SerialName("auto_archive_duration")
    val autoArchiveDuration: ArchiveDuration,
    val type: Optional<ChannelType> = Optional.Missing(),
    val invitable: OptionalBoolean = OptionalBoolean.Missing
)

public data class ListThreadsBySnowflakeRequest(
    val before: Snowflake? = null,
    val limit: Int? = null
)

public data class ListThreadsByTimestampRequest(
    val before: Instant? = null,
    val limit: Int? = null
)
