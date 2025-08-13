package dev.kord.rest.json.request

import dev.kord.common.entity.DiscordGuildTrait
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ModifyGuildProfileRequest(
    val name: Optional<String> = Optional.Missing(),
    val icon: Optional<String?> = Optional.Missing(),
    val description: Optional<String?> = Optional.Missing(),
    @SerialName("brand_color_primary")
    val brandColorPrimary: Optional<String> = Optional.Missing(),
    @SerialName("game_application_ids")
    val gameApplicationIds: Optional<List<Snowflake>> = Optional.Missing(),
    val tag: Optional<String> = Optional.Missing(),
    val badge: Int,
    val badgeColorPrimary: String?,
    val badgeColorSecondary: String?,
    val traits: Optional<List<DiscordGuildTrait>> = Optional.Missing(),
    @SerialName("custom_banner")
    val customBanner: String?
)