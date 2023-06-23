package dev.kord.common.entity

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordTemplate(
    val code: String,
    val name: String,
    val description: String?,
    @SerialName("usage_count")
    val usageCount: Int,
    @SerialName("creator_id")
    val creatorId: Snowflake,
    val creator: DiscordUser,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant,
    @SerialName("source_guild_id")
    val sourceGuildId: Snowflake,
    @SerialName("serialized_source_guild")
    val serializedSourceGuild: DiscordPartialGuild,
    @SerialName("is_dirty")
    val isDirty: Boolean?
)
