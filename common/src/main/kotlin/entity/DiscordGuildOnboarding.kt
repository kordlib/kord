@file:GenerateKordEnum(
    name = "OnboardingPromptType", valueType = INT,
    entries = [
        Entry("MultipleChoice", intValue = 0),
        Entry("Dropdown", intValue = 1)
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @param guildId ID of the guild this onboarding is part of
 * @param prompts Prompts shown during onboarding and in customize community
 * @param defaultChannelIds Channel IDs that members get opted into automatically
 * @param enabled Whether onboarding is enabled in the guild
 */
@Serializable
public data class DiscordGuildOnboarding(
    @SerialName("guild_id") val guildId: Snowflake,
    val prompts: List<DiscordOnboardingPrompt>,
    @SerialName("default_channel_id") val defaultChannelIds: List<Snowflake>,
    val enabled: Boolean
)

/**
 * @param id ID of the prompt
 * @param type Type of prompt
 * @param options Options available within the prompt
 * @param title Title of the prompt
 * @param singleSelect Indicates whether users are limited to selecting one options for the prompt
 * @param required Indicates whether the prompt is required before a user completes the onboarding flow
 * @param inOnboarding Indicates whether the prompt is present in the onboarding flow. If `false`, the prompt will only appear in the Channels & Roles tab
 */
@Serializable
public data class DiscordOnboardingPrompt(
    val id: Snowflake,
    val type: OnboardingPromptType,
    val options: List<DiscordOnboardingPromptOption>,
    val title: String,
    @SerialName("single_select") val singleSelect: Boolean,
    val required: Boolean,
    @SerialName("in_onboarding") val inOnboarding: Boolean
)

/**
 * @param id ID of the prompt option
 * @param channelIds IDs for channels a member is added to when the option is selected
 * @param roleIds IDs for roles assigned to a member when the option is selected
 * @param emoji Emoji of the option
 * @param title Title of the option
 * @param description Description of the option
 */
@Serializable
public data class DiscordOnboardingPromptOption(
    val id: Snowflake,
    @SerialName("channel_ids") val channelIds: List<Snowflake>,
    @SerialName("role_ids") val roleIds: List<Snowflake>,
    val emoji: DiscordEmoji,
    val title: String,
    val description: Optional<String> = Optional.Missing()
)