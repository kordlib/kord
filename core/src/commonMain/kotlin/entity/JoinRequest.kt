package dev.kord.core.entity

import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.DiscordUser
import dev.kord.common.entity.GuildJoinRequestStatus
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.JoinRequestData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import kotlinx.datetime.Instant

public class JoinRequest(
    public val data: JoinRequestData,
    override val kord: Kord,
    public val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity {
    override val id: Snowflake get() = data.id

    public val joinRequestId: Snowflake get() = data.joinRequestId

    public val createdAt: Instant get() = data.createdAt

    public val applicationStatus: GuildJoinRequestStatus get() = data.applicationStatus

    public val guildId: Snowflake get() = data.guildId

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public val formResponses: List<DiscordMemberVerificationFormField>? get() = data.formResponses.value

    public val lastSeen: Instant? get() = data.lastSeen

    public val actionedAt: Snowflake? get() = data.actionedAt.value

    public val actionedAtTime: Instant? = actionedAt?.timestamp

    public val actionedByUser: DiscordUser? get() = data.actionedByUser.value

    public val rejectionReason: String? get() = data.rejectionReason

    public val user: DiscordUser? get() = data.user.value

    public val interviewChannelId: Snowflake? get() = data.interviewChannelId

    public suspend fun getInterviewChannel(): Channel = supplier.getChannelOf(interviewChannelId!!)

    public suspend fun getInterviewChannelOrNull(): Channel? = interviewChannelId?.let { supplier.getChannelOfOrNull(it) }
}