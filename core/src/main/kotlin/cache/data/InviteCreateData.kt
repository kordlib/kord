package dev.kord.core.cache.data

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.TargetUserType
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.entity.optional.mapSnowflake
import dev.kord.gateway.DiscordCreatedInvite
import kotlinx.serialization.Serializable

@Serializable
public data class InviteCreateData(
    val channelId: Snowflake,
    val code: String,
    val createdAt: String,
    val guildId: OptionalSnowflake = OptionalSnowflake.Missing,
    val inviterId: OptionalSnowflake = OptionalSnowflake.Missing,
    val maxAge: Int,
    val maxUses: Int,
    val targetUserId: OptionalSnowflake = OptionalSnowflake.Missing,
    val targetUserType: Optional<TargetUserType> = Optional.Missing(),
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
                targetUser.mapSnowflake { it.id },
                targetUserType,
                temporary,
                uses
            )
        }
    }

}
