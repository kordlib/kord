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

/**
 * A representation of a [Discord Guild Join Request structure]()
 *
 * @param id
 * @param joinRequestId The ID of the join request
 * @param createdAt When the join request was created
 * @param applicationStatus The [status of the join request](GuildJoinRequestStatus)
 * @param guildId The ID of the guild this join request is for
 * @param formResponses A list of [DiscordMemberVerificationFormField] responses from the guild member
 * @param lastSeen When the request was acknowledged by the user
 * @param actionedAt A snowflake representing when the join request was actioned
 * @param actionedByUser The moderator who actioned the join request
 * @param rejectionReason Why the join request was rejected
 * @param userId The ID of the user who created the join request
 * @param user The user who created the join request
 * @param interviewChannelId The ID of the channel where an interview regarding the request can be conducted
 */
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