package com.gitlab.kordlib.core.cache.data

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.TargetUserType
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.OptionalSnowflake
import com.gitlab.kordlib.common.entity.optional.mapSnowflake
import com.gitlab.kordlib.gateway.DiscordCreatedInvite
import com.gitlab.kordlib.gateway.DiscordInviteUser
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InviteCreateData(
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

    companion object {
        fun from(entity: DiscordCreatedInvite): InviteCreateData = with(entity) {
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