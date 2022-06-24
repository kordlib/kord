package dev.kord.core.cache.data

import dev.kord.common.entity.InviteTargetType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapSnowflake
import dev.kord.common.serialization.DurationInSeconds
import dev.kord.gateway.DiscordCreatedInvite
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class InviteCreateData(
    val channelId: Snowflake,
    val code: String,
    val createdAt: Instant,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    val maxAge: DurationInSeconds,
    val maxUses: Int,
    val targetType: Optional<InviteTargetType> = Optional.Missing(),
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    val targetApplication: Optional<PartialApplicationData> = Optional.Missing(),
    @Deprecated("No longer documented. Use 'targetType' instead.", ReplaceWith("this.targetType"))
    val targetUserType: Optional<@Suppress("DEPRECATION") dev.kord.common.entity.TargetUserType> = Optional.Missing(),
    val temporary: Boolean,
    val uses: Int,
) {

    public companion object {
        public fun from(entity: DiscordCreatedInvite): InviteCreateData = with(entity) {
            InviteCreateData(
                channelId,
                code,
                createdAt,
                guildId,
                inviter.mapSnowflake { it.id },
                maxAge,
                maxUses,
                targetType,
                targetUser.mapSnowflake { it.id },
                targetApplication.map { PartialApplicationData.from(it) },
                @Suppress("DEPRECATION")
                targetUserType,
                temporary,
                uses,
            )
        }
    }
}
