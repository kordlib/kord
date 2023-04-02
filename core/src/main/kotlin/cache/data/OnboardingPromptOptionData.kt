package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.DiscordOnboardingPromptOption
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import kotlinx.serialization.Serializable

@Serializable
public data class OnboardingPromptOptionData(
    val id: Snowflake,
    val channelIds: List<Snowflake>,
    val roleIds: List<Snowflake>,
    val emoji: DiscordEmoji,
    val title: String,
    val description: Optional<String> = Optional.Missing()
) {
    public companion object {
        public fun from(entity: DiscordOnboardingPromptOption): OnboardingPromptOptionData = with(entity) {
            OnboardingPromptOptionData(
                id,
                channelIds,
                roleIds,
                emoji,
                title,
                description
            )
        }
    }
}