package dev.kord.core.entity

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.DiscordMemberVerificationFormField
import dev.kord.common.entity.GuildJoinRequestStatus
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.JoinRequestData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import kotlin.time.Instant

@KordPreview
public class JoinRequest(
    public val data: JoinRequestData,
    override val kord: Kord,
    public val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity {
    override val id: Snowflake get() = data.id

    /**
     * The ID of the join request
     */
    public val joinRequestId: Snowflake get() = data.joinRequestId

    /**
     * When the join request was created
     */
    public val createdAt: Instant get() = data.createdAt

    /**
     * The status of the application
     *
     * @see [GuildJoinRequestStatus]
     */
    public val applicationStatus: GuildJoinRequestStatus get() = data.applicationStatus

    /**
     * The ID of the guild this join request is for
     */
    public val guildId: Snowflake get() = data.guildId

    /**
     * Requests the [Guild] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests the [Guild] with the given [id], returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Responses to the verification questions
     */
    public val formResponses: List<DiscordMemberVerificationFormField>? get() = data.formResponses.value

    /**
     * When the request was acknowledged by the user
     */
    public val lastSeen: Instant? get() = data.lastSeen

    /**
     * A snowflake representing when the join request was actioned
     */
    public val actionedAt: Snowflake? get() = data.actionedAt.value

    /**
     * The [actionedAt] snowflake formated as an [Instant]
     */
    public val actionedAtTime: Instant? = actionedAt?.timestamp

    /**
     * The moderator that actioned the request
     */
    public val actionedByUser: UserBehavior? get() = data.actionedByUser.value?.id?.let { UserBehavior(it, kord) }

    /**
     * The reason given for rejecting the join request
     */
    public val rejectionReason: String? get() = data.rejectionReason

    /**
     * The Id of the user who created this join request
     */
    public val userId: Snowflake get() = data.userId

    /**
     * The user that created this join request
     */
    public val user: UserBehavior? get() = data.user.value?.id?.let { UserBehavior(it, kord)}


    /**
     * The ID of the channel where an interview regarding this join request may be conducted
     */
    public val interviewChannelId: Snowflake? get() = data.interviewChannelId

    /**
     * Requests the [Channel] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     * @throws EntityNotFoundException if the channel is null.
     */
    public suspend fun getInterviewChannel(): Channel = supplier.getChannel(interviewChannelId!!)

    /**
     * Requests the [Channel] with the given [id], returns `null` when the channel isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend fun getInterviewChannelOrNull(): Channel? = interviewChannelId?.let { supplier.getChannelOrNull(it) }
}