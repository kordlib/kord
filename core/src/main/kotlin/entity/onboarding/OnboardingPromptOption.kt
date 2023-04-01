package dev.kord.core.entity.onboarding

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.core.cache.data.OnboardingPromptOptionData

public class OnboardingPromptOption(
    public val data: OnboardingPromptOptionData,
) {
    public val id: Snowflake get() = data.id

    public val channelIds: List<Snowflake> get() = data.channelIds

    public val roleIds: List<Snowflake> get() = data.roleIds

    public val emoji: DiscordEmoji get() = data.emoji

    public val title: String get() = data.title

    public val description: String? get() = data.description.value
}