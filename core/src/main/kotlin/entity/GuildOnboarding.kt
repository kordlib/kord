package dev.kord.core.entity

import dev.kord.common.entity.*
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

/**
 * Represents the [onboarding](https://support.discord.com/hc/en-us/articles/11074987197975-Community-Onboarding-FAQ)
 * flow for a [Guild].
 */
public class GuildOnboarding(
    public val data: DiscordGuildOnboarding,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {
    /** The ID of the [Guild] this onboarding is part of. */
    public val guildId: Snowflake get() = data.guildId

    /** The [GuildBehavior] for this onboarding. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** The [OnboardingPrompt]s shown during onboarding and in customize community. */
    public val prompts: List<OnboardingPrompt> get() = data.prompts.map { OnboardingPrompt(it, guildId, kord) }

    /** The IDs of the [channels][TopGuildChannel] that [Member]s get opted into automatically. */
    public val defaultChannelIds: List<Snowflake> get() = data.defaultChannelIds

    /** Whether onboarding is enabled in the [guild]. */
    public val isEnabled: Boolean get() = data.enabled

    /**
     * Requests the [Guild] this onboarding is part of.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests the [Guild] this onboarding is part of, returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests the [channels][TopGuildChannel] that [Member]s get opted into automatically.
     *
     * The returned [Flow] is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val defaultChannels: Flow<TopGuildChannel> =
        supplier.getGuildChannels(guildId).filter { it.id in defaultChannelIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildOnboarding =
        GuildOnboarding(data, kord, strategy.supply(kord))
}

/**
 * Represents a prompt for a [GuildOnboarding].
 *
 * @property guildId The ID of the [Guild] the [onboarding][GuildOnboarding] is part of.
 */
public class OnboardingPrompt(
    public val data: DiscordOnboardingPrompt,
    public val guildId: Snowflake,
    override val kord: Kord,
) : KordEntity {
    override val id: Snowflake get() = data.id

    /** The [type][OnboardingPromptType] of this prompt. */
    public val type: OnboardingPromptType get() = data.type

    /** The [OnboardingPromptOption]s available within this prompt. */
    public val options: List<OnboardingPromptOption> get() = data.options.map { OnboardingPromptOption(it, guildId, kord) }

    /** The title of this prompt. */
    public val title: String get() = data.title

    /** Indicates whether users are limited to selecting one option for this prompt. */
    public val isSingleSelect: Boolean get() = data.singleSelect

    /** Indicates whether this prompt is required before a user completes the onboarding flow. */
    public val isRequired: Boolean get() = data.required

    /**
     * Indicates whether this prompt is present in the onboarding flow. If `false`, this prompt will only appear in the
     * Channels & Roles tab.
     */
    public val isInOnboarding: Boolean get() = data.inOnboarding
}

/**
 * Represents an option for a [OnboardingPrompt].
 *
 * @property guildId The ID of the [Guild] the [onboarding][GuildOnboarding] is part of.
 */
public class OnboardingPromptOption(
    public val data: DiscordOnboardingPromptOption,
    public val guildId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordEntity, Strategizable {
    override val id: Snowflake get() = data.id

    /** The IDs for the [channels][TopGuildChannel] a [Member] is added to when this option is selected. */
    public val channelIds: List<Snowflake> get() = data.channelIds

    /** The IDs for the [Role]s assigned to a [Member] when this option is selected. */
    public val roleIds: List<Snowflake> get() = data.roleIds

    /** The [Emoji] of this option. */
    public val emoji: Emoji get() = when (data.emoji.id) {
        null -> Emoji.Standard(data.emoji.name!!)
        else -> Emoji.Guild(data.emoji.id!!, data.emoji.name ?: "", data.emoji.animated.discordBoolean)
    }

    /** The title of this option. */
    public val title: String get() = data.title

    /** The description of this option. */
    public val description: String? get() = data.description

    /**
     * Requests the [channels][TopGuildChannel] a [Member] is added to when this option is selected.
     *
     * The returned [Flow] is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val channels: Flow<TopGuildChannel> get() = supplier.getGuildChannels(guildId).filter { it.id in channelIds }

    /**
     * Requests the [Role]s assigned to a [Member] when this option is selected.
     *
     * The returned [Flow] is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val roles: Flow<Role> get() = supplier.getGuildRoles(guildId).filter { it.id in roleIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): OnboardingPromptOption =
        OnboardingPromptOption(data, guildId, kord, strategy.supply(kord))
}
