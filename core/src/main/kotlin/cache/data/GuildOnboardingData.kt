package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordGuildOnboarding
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

/**
 * Represents a Guild Onboarding object
 *
 * @param guildId The ID of the guild this onboarding is part of
 * @param prompts Prompts show during onboarding and in customize community
 * @param defaultChannelIds Channel IDs that members get opted into automatically
 * @param enabled Whether onboarding is enabled in the guild
 */
@Serializable
public data class GuildOnboardingData(
    val guildId: Snowflake,
    val prompts: List<OnboardingPromptData>,
    val defaultChannelIds: List<Snowflake>,
    val enabled: Boolean
) {
    public companion object {
        public fun from(entity: DiscordGuildOnboarding): GuildOnboardingData = with(entity) {
            GuildOnboardingData(
                guildId,
                prompts.map { OnboardingPromptData.from(it) },
                defaultChannelIds,
                enabled
            )
        }
    }
}
