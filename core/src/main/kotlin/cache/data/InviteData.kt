package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.*
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class InviteData(
    val code: String,
    val guild: Optional<PartialGuildData> = Optional.Missing(),
    val channelId: Snowflake?,
    val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    val targetApplication: Optional<PartialApplicationData> = Optional.Missing(),
    @Deprecated("This is no longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    val targetUserType: Optional<@Suppress("DEPRECATION") TargetUserType> = Optional.Missing(),
    val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    val approximateMemberCount: OptionalInt = OptionalInt.Missing,
    val expiresAt: Optional<Instant?> = Optional.Missing(),
    val guildScheduledEvent: Optional<GuildScheduledEventData> = Optional.Missing(),
    val metadata: Optional<Metadata> = Optional.Missing(),
) {

    @Serializable
    public data class Metadata(
        val uses: Int,
        val maxUses: Int,
        val maxAge: Int,
        val temporary: Boolean,
        val createdAt: Instant,
    )

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
                metadata = Optional.Missing(),
            )
        }

        public fun from(entity: DiscordInviteWithMetadata): InviteData = with(entity) {
            InviteData(
                code,
                guild = guild.map { PartialGuildData.from(it) },
                channelId = channel?.id,
                inviterId = inviter.mapSnowflake { it.id },
                targetType,
                targetUserId = targetUser.mapSnowflake { it.id },
                targetApplication = targetApplication.map { PartialApplicationData.from(it) },
                targetUserType = Optional.Missing(), // deprecated
                approximatePresenceCount,
                approximateMemberCount,
                expiresAt,
                guildScheduledEvent = guildScheduledEvent.map { GuildScheduledEventData.from(it) },
                metadata = Metadata(uses, maxUses, maxAge, temporary, createdAt).optional()
            )
        }
    }
}
