package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import dev.kord.common.serialization.DurationInSeconds
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

public sealed interface BaseInviteData {
    public val code: String
    public val guild: Optional<PartialGuildData>
    public val channelId: Snowflake?
    public val inviterId: OptionalSnowflake
    public val targetType: Optional<InviteTargetType>
    public val targetUserId: OptionalSnowflake
    public val targetApplication: Optional<PartialApplicationData>
    public val approximatePresenceCount: OptionalInt
    public val approximateMemberCount: OptionalInt
    public val expiresAt: Optional<Instant?>
    public val guildScheduledEvent: Optional<GuildScheduledEventData>
}

@Serializable
public data class InviteData(
    override val code: String,
    override val guild: Optional<PartialGuildData> = Optional.Missing(),
    override val channelId: Snowflake?,
    override val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val targetType: Optional<InviteTargetType> = Optional.Missing(),
    override val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val targetApplication: Optional<PartialApplicationData> = Optional.Missing(),
    @Deprecated("This is no longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    val targetUserType: Optional<@Suppress("DEPRECATION") TargetUserType> = Optional.Missing(),
    override val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    override val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    override val expiresAt: Optional<Instant?> = Optional.Missing(),
    override val guildScheduledEvent: Optional<GuildScheduledEventData> = Optional.Missing(),
) : BaseInviteData {

    public companion object {

        public fun from(entity: DiscordInvite): InviteData = with(entity) {
            InviteData(
                code,
                guild = guild.map { PartialGuildData.from(it) },
                channelId = channel?.id,
                inviterId = inviter.mapSnowflake { it.id },
                targetType,
                targetUserId = targetUser.mapSnowflake { it.id },
                targetApplication = targetApplication.map { PartialApplicationData.from(it) },
                @Suppress("DEPRECATION")
                targetUserType,
                approximatePresenceCount,
                approximateMemberCount,
                expiresAt,
                guildScheduledEvent = guildScheduledEvent.map { GuildScheduledEventData.from(it) },
            )
        }
    }
}

@Serializable
public data class InviteWithMetadataData(
    override val code: String,
    override val guild: Optional<PartialGuildData> = Optional.Missing(),
    override val channelId: Snowflake?,
    override val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val targetType: Optional<InviteTargetType> = Optional.Missing(),
    override val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    override val targetApplication: Optional<PartialApplicationData> = Optional.Missing(),
    override val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    override val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    override val expiresAt: Optional<Instant?> = Optional.Missing(),
    override val guildScheduledEvent: Optional<GuildScheduledEventData> = Optional.Missing(),
    val uses: Int,
    val maxUses: Int,
    val maxAge: DurationInSeconds,
    val temporary: Boolean,
    val createdAt: Instant,
) : BaseInviteData {

    public companion object {

        public fun from(entity: DiscordInviteWithMetadata): InviteWithMetadataData = with(entity) {
            InviteWithMetadataData(
                code,
                guild = guild.map { PartialGuildData.from(it) },
                channelId = channel?.id,
                inviterId = inviter.mapSnowflake { it.id },
                targetType,
                targetUserId = targetUser.mapSnowflake { it.id },
                targetApplication = targetApplication.map { PartialApplicationData.from(it) },
                approximatePresenceCount,
                approximateMemberCount,
                expiresAt,
                guildScheduledEvent = guildScheduledEvent.map { GuildScheduledEventData.from(it) },
                uses,
                maxUses,
                maxAge,
                temporary,
                createdAt,
            )
        }
    }
}
