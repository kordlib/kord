package dev.kord.core.entity.onboarding

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.DiscordOnboardingPromptOption
import dev.kord.common.entity.Snowflake

public class OnboardingPromptOption(
        public val data: DiscordOnboardingPromptOption,
) {
    public val id: Snowflake get() = data.id

    public val channelIds: List<Snowflake> get() = data.channelIds

    public val roleIds: List<Snowflake> get() = data.roleIds

    public val emoji: DiscordEmoji get() = data.emoji

    public val title: String get() = data.title

    public val description: String? get() = data.description.value
}