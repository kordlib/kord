package dev.kord.core.entity.onboarding

import dev.kord.common.entity.OnboardingPromptType
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.OnboardingPromptData
import dev.kord.core.cache.data.OnboardingPromptOptionData

public class OnboardingPrompt(
    public val data: OnboardingPromptData,
) {
    public val id: Snowflake get() = data.id

    public val type: OnboardingPromptType get() = data.type

    public val options: List<OnboardingPromptOptionData> get() = data.options

    public val title: String get() = data.title

    public val singleSelect: Boolean get() = data.singleSelect

    public val required: Boolean get() = data.required

    public val inOnboarding: Boolean get() = data.inOnboarding
}