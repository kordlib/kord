package dev.kord.core.entity.onboarding

import dev.kord.common.entity.DiscordOnboardingPrompt
import dev.kord.common.entity.OnboardingPromptType
import dev.kord.common.entity.Snowflake

public class OnboardingPrompt(
        public val data: DiscordOnboardingPrompt,
) {
    public val id: Snowflake get() = data.id

    public val type: OnboardingPromptType get() = data.type

    public val options: List<OnboardingPromptOption> get() = data.options.map { OnboardingPromptOption(it) }

    public val title: String get() = data.title

    public val singleSelect: Boolean get() = data.singleSelect

    public val required: Boolean get() = data.required

    public val inOnboarding: Boolean get() = data.inOnboarding
}