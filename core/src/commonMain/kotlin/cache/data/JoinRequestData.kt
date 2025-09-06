package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildJoinRequest
import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.GuildJoinRequestStatus
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
public data class JoinRequestData(
    val id: Snowflake,
    val joinRequestId: Snowflake,
    val createdAt: Instant,
    val applicationStatus: GuildJoinRequestStatus,
    val guildId: Snowflake,
    val formResponses: Optional<List<DiscordMemberVerificationFormField>?> = Optional.Missing(),
    val lastSeen: Instant? = null,
    val actionedAt: OptionalSnowflake = OptionalSnowflake.Missing,
    val actionedByUser: Optional<DiscordUser> = Optional.Missing(),
    val rejectionReason: String? = null,
    val userId: Snowflake,
    val user: Optional<DiscordUser> = Optional.Missing(),
    val interviewChannelId: Snowflake? = null
) {
    public companion object {
        public fun from(entity: DiscordGuildJoinRequest): JoinRequestData = with(entity) {
            JoinRequestData(
                id = id,
                joinRequestId = joinRequestId,
                createdAt = createdAt,
                applicationStatus = applicationStatus,
                guildId = guildId,
                formResponses = formResponses,
                lastSeen = lastSeen,
                actionedAt = actionedAt,
                actionedByUser = actionedByUser,
                rejectionReason = rejectionReason,
                userId = userId,
                user = user,
                interviewChannelId = interviewChannelId

            )
        }
    }
}

public fun DiscordGuildJoinRequest.toData(): JoinRequestData = JoinRequestData.from(this)