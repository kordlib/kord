package dev.kord.core.cache.data

import dev.kord.common.entity.*
import kotlinx.serialization.Serializable

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

@Serializable
public data class OnboardingPromptData(
        val id: Snowflake,
        val type: OnboardingPromptType,
        val options: List<OnboardingPromptOptionData>,
        val title: String,
        val singleSelect: Boolean,
        val required: Boolean,
        val inOnboarding: Boolean
) {
    public companion object {
        public fun from(entity: DiscordOnboardingPrompt): OnboardingPromptData = with(entity) {
            OnboardingPromptData(
                    id,
                    type,
                    options.map { OnboardingPromptOptionData.from(it) },
                    title,
                    singleSelect,
                    required,
                    inOnboarding
            )
        }
    }
}

@Serializable
public data class OnboardingPromptOptionData(
        val id: Snowflake,
        val channelIds: List<Snowflake>,
        val roleIds: List<Snowflake>,
        val emoji: DiscordEmoji,
        val title: String,
        val description: String?
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
