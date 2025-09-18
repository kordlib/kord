package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordInteractionMetadata
import dev.kord.common.entity.DiscordMessageInteraction
import dev.kord.common.entity.IntegrationOwners
import dev.kord.common.entity.InteractionType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.Optional.Missing.Companion.invoke
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.Serializable

@Serializable
public data class MessageInteractionData(
    val id: Snowflake,
    val type: InteractionType,
    val name: String,
    val user: Snowflake
) {
    public companion object {
        public fun from(entity: DiscordMessageInteraction): MessageInteractionData = with(entity) {
            MessageInteractionData(id, type, name, user.id)
        }
    }
}

@Serializable
public data class InteractionMetadataData(
    val id: Snowflake,
    val type: InteractionType,
    val user: UserData,
    val authorizingIntegrationOwners: IntegrationOwners,
    val originalResponseMessageId: OptionalSnowflake = OptionalSnowflake.Missing,
    val interactedMessageId: OptionalSnowflake = OptionalSnowflake.Missing,
    val triggeringInteractionMetadata: Optional<DiscordInteractionMetadata> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordInteractionMetadata): InteractionMetadataData = with(entity) {
            InteractionMetadataData(
                id,
                type,
                UserData.from(entity.user),
                authorizingIntegrationOwners,
                originalResponseMessageId,
                interactedMessageId,
                triggeringInteractionMetadata
            )
        }
    }
}
