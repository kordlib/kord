package dev.kord.core.cache.data

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.OptionalBoolean
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
    val syncing: Boolean,
    val roleId: Snowflake,
    val enableEmoticons: OptionalBoolean = OptionalBoolean.Missing,
    val expireBehavior: IntegrationExpireBehavior,
    val expireGracePeriod: DurationInDays,
    val user: DiscordUser,
    val account: IntegrationsAccountData,
    val syncedAt: Instant,
    val subscriberCount: Int,
    val revoked: Boolean,
    val application: IntegrationApplication,
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
