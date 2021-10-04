package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordRoleTags
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
public data class RoleTagsData(
    val botId: OptionalSnowflake = OptionalSnowflake.Missing,
    val integrationId: OptionalSnowflake = OptionalSnowflake.Missing,
    val premiumSubscriber: Boolean,
) {
    public companion object {
        public fun from(entity: DiscordRoleTags): RoleTagsData = with(entity) {
            RoleTagsData(botId, integrationId, premiumSubscriber is Optional.Null)
        }
    }
}
