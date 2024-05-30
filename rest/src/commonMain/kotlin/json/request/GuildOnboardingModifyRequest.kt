package dev.kord.rest.json.request

import dev.kord.common.entity.*
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class GuildOnboardingModifyRequest(
    val prompts: Optional<List<OnboardingPromptRequest>> = Optional.Missing,
    @SerialName("default_channel_ids")
    val defaultChannelIds: Optional<List<Snowflake>> = Optional.Missing,
    val enabled: OptionalBoolean = OptionalBoolean.Missing,
    val mode: Optional<OnboardingMode> = Optional.Missing,
)

@Serializable
public data class OnboardingPromptRequest(
    val id: Snowflake,
    val type: OnboardingPromptType,
    val options: List<OnboardingPromptOptionRequest>,
    val title: String,
    @SerialName("single_select")
    val singleSelect: Boolean,
    val required: Boolean,
    @SerialName("in_onboarding")
    val inOnboarding: Boolean,
)

@Serializable
public data class OnboardingPromptOptionRequest(
    @SerialName("channel_ids")
    val channelIds: List<Snowflake>,
    @SerialName("role_ids")
    val roleIds: List<Snowflake>,
    val title: String,
    val description: String?,
)
