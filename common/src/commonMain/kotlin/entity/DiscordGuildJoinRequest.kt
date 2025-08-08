@file:Generate(
    STRING_KORD_ENUM, name = "GuildJoinRequestStatus",
    docUrl = "", entries = [
        Entry("Started", stringValue = "STARTED", kDoc = "The request is started but not submitted"),
        Entry("Submitted", stringValue = "SUBMITTED", kDoc = "The request has been submitted"),
        Entry("Rejected", stringValue = "REJECTED", kDoc = "The request has been rejected"),
        Entry("Approved", stringValue = "APPROVED", kDoc = "The request has been approved")
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.*
import dev.kord.ksp.Generate.Entry
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildJoinRequest(
    val id: Snowflake,
    @SerialName("join_request_id")
    val joinRequestId: Snowflake,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("application_status")
    val applicationStatus: GuildJoinRequestStatus,
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("form_responses")
    val formResponses: Optional<List<DiscordMemberVerificationFormField>?> = Optional.Missing(),
    @SerialName("last_seen")
    val lastSeen: Instant?,
    @SerialName("actioned_at")
    val actionedAt: OptionalSnowflake = OptionalSnowflake.Missing,
    @SerialName("actioned_by_user")
    val actionedByUser: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("rejection_reason")
    val rejectionReason: String?,
    @SerialName("user_id")
    val userId: Snowflake,
    val user: Optional<DiscordUser> = Optional.Missing(),
    @SerialName("interview_channel_id")
    val interviewChannelId: Snowflake?
)