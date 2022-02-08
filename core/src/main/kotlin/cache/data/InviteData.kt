package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordInvite
import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TargetUserType
import dev.kord.common.entity.optional.*
import kotlinx.serialization.Serializable

@Serializable
public data class InviteData(
    val code: String,
    val guild: Optional<PartialGuildData> = Optional.Missing(),
    val channelId: Snowflake?,
    val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    @Deprecated("This is no longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    val targetUserType: Optional<@Suppress("DEPRECATION") TargetUserType> = Optional.Missing(),
    val approximatePresenceCount: OptionalInt = OptionalInt.Missing,
    val approximateMemberCount: OptionalInt = OptionalInt.Missing,
) {

    public companion object {

        public fun from(entity: DiscordInvite): InviteData = with(entity) {
            InviteData(
                code,
                guild = guild.map { PartialGuildData.from(it) },
                channelId = channel?.id,
                inviterId = inviter.mapSnowflake { it.id },
                targetType,
                targetUserId = targetUser.mapSnowflake { it.id },
                @Suppress("DEPRECATION")
                targetUserType,
                approximatePresenceCount,
                approximateMemberCount,
            )
        }
    }
}
