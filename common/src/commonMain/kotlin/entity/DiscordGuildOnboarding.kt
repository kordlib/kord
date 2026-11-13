@file:Generate(
    INT_KORD_ENUM, name = "OnboardingMode",
    kDoc = "Defines the criteria used to satisfy Onboarding constraints that are required for enabling.",
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-onboarding-object-onboarding-mode",
    entries = [
        Entry("Default", intValue = 0, kDoc = "Counts only Default Channels towards constraints."),
        Entry("Advanced", intValue = 1, kDoc = "Counts Default Channels and Questions towards constraints."),
    ],
)

@file:Generate(
    INT_KORD_ENUM, name = "OnboardingPromptType",
    docUrl = "https://discord.com/developers/docs/resources/guild#guild-onboarding-object-prompt-types",
    entries = [
        Entry("MultipleChoice", intValue = 0),
        Entry("Dropdown", intValue = 1),
    ],
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildOnboarding(
    @SerialName("guild_id") val guildId: Snowflake,
    val prompts: List<DiscordOnboardingPrompt>,
    @SerialName("default_channel_ids") val defaultChannelIds: List<Snowflake>,
    val enabled: Boolean,
    val mode: OnboardingMode,
)

@Serializable
public data class DiscordOnboardingPrompt(
    val id: Snowflake,
    val type: OnboardingPromptType,
    val options: List<DiscordOnboardingPromptOption>,
    val title: String,
    @SerialName("single_select") val singleSelect: Boolean,
    val required: Boolean,
    @SerialName("in_onboarding") val inOnboarding: Boolean,
)

@Serializable
public data class DiscordOnboardingPromptOption(
    val id: Snowflake,
    @SerialName("channel_ids") val channelIds: List<Snowflake>,
    @SerialName("role_ids") val roleIds: List<Snowflake>,
    val emoji: DiscordEmoji,
    val title: String,
    val description: String?,
)
