package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalInt
import dev.kord.common.entity.optional.OptionalSnowflake
import dev.kord.common.serialization.DurationInDays
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
public data class IntegrationData(
    val id: Snowflake,
    val guildId: Snowflake,
    val name: String,
    val type: String,
    val enabled: Boolean,
    val syncing: OptionalBoolean,
    val roleId: OptionalSnowflake,
    val enableEmoticons: OptionalBoolean = OptionalBoolean.Missing,
    val expireBehavior: Optional<IntegrationExpireBehavior>,
    val expireGracePeriod: Optional<DurationInDays>,
    val user: Optional<DiscordUser>,
    val account: IntegrationsAccountData,
    val syncedAt: Optional<Instant>,
    val subscriberCount: OptionalInt,
    val revoked: OptionalBoolean,
    val application: Optional<IntegrationApplication>,
) {

    public companion object {

        public fun from(guildId: Snowflake, response: DiscordIntegration): IntegrationData = with(response) {
            IntegrationData(
                id,
                guildId,
                name,
                type,
                enabled,
                syncing,
                roleId,
                enableEmoticons,
                expireBehavior,
                expireGracePeriod,
                user,
                IntegrationsAccountData.from(account),
                syncedAt,
                subscriberCount,
                revoked,
                application
            )

        }

    }

}
