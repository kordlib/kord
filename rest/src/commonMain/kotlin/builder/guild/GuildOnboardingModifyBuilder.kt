package dev.kord.rest.builder.guild

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.OnboardingMode
import dev.kord.common.entity.OnboardingPromptType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.OptionalBoolean
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.map
import dev.kord.common.entity.optional.mapCopy
import dev.kord.rest.builder.AuditRequestBuilder
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.GuildOnboardingModifyRequest
import dev.kord.rest.json.request.OnboardingPromptOptionRequest
import dev.kord.rest.json.request.OnboardingPromptRequest
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

@KordDsl
public class GuildOnboardingModifyBuilder : AuditRequestBuilder<GuildOnboardingModifyRequest> {
    override var reason: String? = null

    private var _prompts: Optional<MutableList<OnboardingPromptBuilder>> = Optional.Missing()

    /** The prompts shown during onboarding and in customize community. */
    public var prompts: MutableList<OnboardingPromptBuilder>? by ::_prompts.delegate()

    private var _defaultChannelIds: Optional<MutableList<Snowflake>> = Optional.Missing()

    /** The IDs of the channels that members get opted into automatically. */
    public var defaultChannelIds: MutableList<Snowflake>? by ::_defaultChannelIds.delegate()

    private var _enabled: OptionalBoolean = OptionalBoolean.Missing

    /** Whether onboarding is enabled in the guild. */
    public var enabled: Boolean? by ::_enabled.delegate()

    private var _mode: Optional<OnboardingMode> = Optional.Missing()

    /** Current [mode][OnboardingMode] of onboarding. */
    public var mode: OnboardingMode? by ::_mode.delegate()

    override fun toRequest(): GuildOnboardingModifyRequest = GuildOnboardingModifyRequest(
        prompts = _prompts.map { it.map(OnboardingPromptBuilder::toRequest) },
        defaultChannelIds = _defaultChannelIds.mapCopy(),
        enabled = _enabled,
        mode = _mode,
    )
}

/** Add a [channelId] to [defaultChannelIds][GuildOnboardingModifyBuilder.defaultChannelIds]. */
public fun GuildOnboardingModifyBuilder.defaultChannelId(channelId: Snowflake) {
    defaultChannelIds?.add(channelId) ?: run { defaultChannelIds = mutableListOf(channelId) }
}

/**
 * Add a prompt to [prompts][GuildOnboardingModifyBuilder.prompts].
 *
 * @param type The [type][OnboardingPromptType] of the prompt.
 * @param title The title of the prompt.
 * @param singleSelect Indicates whether users are limited to selecting one option for the prompt.
 * @param required Indicates whether the prompt is required before a user completes the onboarding flow.
 * @param inOnboarding Indicates whether the prompt is present in the onboarding flow. If `false`, the prompt will only
 * appear in the Channels & Roles tab.
 */
public inline fun GuildOnboardingModifyBuilder.prompt(
    type: OnboardingPromptType,
    title: String,
    singleSelect: Boolean,
    required: Boolean,
    inOnboarding: Boolean,
    builder: OnboardingPromptBuilder.() -> Unit,
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    val prompt = OnboardingPromptBuilder(type, title, singleSelect, required, inOnboarding).apply(builder)
    prompts?.add(prompt) ?: run { prompts = mutableListOf(prompt) }
}


@KordDsl
public class OnboardingPromptBuilder(
    /** The [type][OnboardingPromptType] of the prompt. */
    public var type: OnboardingPromptType,
    /** The title of the prompt. */
    public var title: String,
    /** Indicates whether users are limited to selecting one option for the prompt. */
    public var singleSelect: Boolean,
    /** Indicates whether the prompt is required before a user completes the onboarding flow. */
    public var required: Boolean,
    /**
     * Indicates whether the prompt is present in the onboarding flow. If `false`, the prompt will only appear in the
     * Channels & Roles tab.
     */
    public var inOnboarding: Boolean,
) : RequestBuilder<OnboardingPromptRequest> {

    /** The ID of the prompt. */
    public var id: Snowflake? = null

    /** The options available within the prompt. */
    public val options: MutableList<OnboardingPromptOptionBuilder> = mutableListOf()

    override fun toRequest(): OnboardingPromptRequest = OnboardingPromptRequest(
        id = id ?: Snowflake.min, // it needs an ID, if we don't have one yet, pass 0
        type = type,
        options = options.map(OnboardingPromptOptionBuilder::toRequest),
        title = title,
        singleSelect = singleSelect,
        required = required,
        inOnboarding = inOnboarding,
    )
}

/**
 * Add an option to [options][OnboardingPromptBuilder.options].
 *
 * @param title The title of the option.
 */
public inline fun OnboardingPromptBuilder.option(
    title: String,
    builder: OnboardingPromptOptionBuilder.() -> Unit,
) {
    contract { callsInPlace(builder, EXACTLY_ONCE) }
    options.add(OnboardingPromptOptionBuilder(title).apply(builder))
}


@KordDsl
public class OnboardingPromptOptionBuilder(
    /** The title of the option. */
    public var title: String,
) : RequestBuilder<OnboardingPromptOptionRequest> {

    /** The IDs for the channels a member is added to when the option is selected. */
    public val channelIds: MutableList<Snowflake> = mutableListOf()

    /** The IDs for the roles assigned to a member when the option is selected. */
    public val roleIds: MutableList<Snowflake> = mutableListOf()

    /** The description of the option. */
    public var description: String? = null

    override fun toRequest(): OnboardingPromptOptionRequest = OnboardingPromptOptionRequest(
        channelIds = channelIds.toList(),
        roleIds = roleIds.toList(),
        title = title,
        description = description,
    )
}
