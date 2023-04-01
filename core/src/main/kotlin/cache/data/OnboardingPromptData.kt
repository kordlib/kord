package dev.kord.core.cache.data

import dev.kord.common.entity.DiscordOnboardingPrompt
import dev.kord.common.entity.OnboardingPromptType
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable

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
