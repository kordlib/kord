package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.OptionalSnowflake
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DiscordDiscoveryMetadata(
    @SerialName("guild_id")
    val guildId: Snowflake,
    val keywords: Optional<List<String>>,
    @SerialName("emoji_discoverability_enabled")
    val emojiDiscoverabilityEnabled: Boolean,
    @SerialName("partner_actioned_timestamp")
    val partnerActionedTimestamp: Optional<String>,
    @SerialName("partner_application_timestamp")
    val partnerApplicationTimestamp: Optional<String>,
    @SerialName("categories_id")
    val categoriesIds: List<Snowflake>
)

@Serializable
data class DiscordDiscoveryCategory(
    val id: Int,
    val name: Name,
    @SerialName("is_primary")
    val isPrimary: Boolean
) {
    @Serializable
    data class Name(
        val default: String,
        val localizations: Optional<Map<String, String>>
    )
}

@Serializable
data class DiscoveryTermValidationResponse(val valid: Boolean)

@Serializable
data class PartialDiscordDiscoveryMetadata(
    @SerialName("primary_category_id")
    val primaryCategoryId: OptionalSnowflake = OptionalSnowflake.Missing,
    val keywords: Optional<List<String>> = Optional.Missing(),
    @SerialName("emoji_discoverability_enabled")
    val emojiDiscoverabilityEnabled: OptionalBoolean = OptionalBoolean.Missing
)

@Serializable
data class AddGuildDiscoverySubCategoryResponse(
    @SerialName("guild_id")
    val guildId: Snowflake,
    @SerialName("category_id")
    val categoryId: Snowflake
)
