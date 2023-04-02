@file:GenerateKordEnum(
        // TODO Add doc entry
        name = "OnboardingPromptType", valueType = INT,
        entries = [
            Entry("MultipleChoice", intValue = 0),
            Entry("Dropdown", intValue = 1)
        ]
)

package dev.kord.common.entity

import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordGuildOnboarding(
        @SerialName("guild_id") val guildId: Snowflake,
        val prompts: List<DiscordOnboardingPrompt>,
        @SerialName("default_channel_ids") val defaultChannelIds: List<Snowflake>,
        val enabled: Boolean
)

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

@Serializable
public data class DiscordOnboardingPromptOption(
        val id: Snowflake,
        @SerialName("channel_ids") val channelIds: List<Snowflake>,
        @SerialName("role_ids") val roleIds: List<Snowflake>,
        val emoji: DiscordEmoji,
        val title: String,
        val description: String?
)