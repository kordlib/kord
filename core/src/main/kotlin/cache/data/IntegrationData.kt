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
    val syncing: OptionalBoolean = OptionalBoolean.Missing,
    val roleId: OptionalSnowflake = OptionalSnowflake.Missing,
    val enableEmoticons: OptionalBoolean = OptionalBoolean.Missing,
    val expireBehavior: Optional<IntegrationExpireBehavior> = Optional.Missing(),
    val expireGracePeriod: Optional<DurationInDays> = Optional.Missing(),
    val user: Optional<DiscordUser> = Optional.Missing(),
    val account: IntegrationsAccountData,
    val syncedAt: Optional<Instant> = Optional.Missing(),
    val subscriberCount: OptionalInt = OptionalInt.Missing,
    val revoked: OptionalBoolean = OptionalBoolean.Missing,
    val application: Optional<IntegrationApplication> = Optional.Missing(),
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
