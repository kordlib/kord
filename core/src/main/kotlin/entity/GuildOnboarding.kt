package dev.kord.core.entity

import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.OnboardingPromptType
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.GuildOnboardingData
import dev.kord.core.cache.data.OnboardingPromptData
import dev.kord.core.cache.data.OnboardingPromptOptionData
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

/**
 * Represents an instance of a Guild Onboarding
 *
 * @param data The data for the onboarding.
 */
public class GuildOnboarding(
        public val data: GuildOnboardingData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {
    /** The ID of the guild the onboarding is for. */
    public val guildId: Snowflake get() = data.guildId

    /** The [GuildBehavior] for the onboarding. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** A list of [OnboardingPrompt]s for the onboarding. */
    public val prompts: List<OnboardingPrompt> get() = data.prompts.map { OnboardingPrompt(it, guildId, kord) }

    /** A list of [Snowflake] channel Ids that are defaulted for the onboarding. */
    public val defaultChannelIds: List<Snowflake> get() = data.defaultChannelIds

    /** Whether the onboarding is enabled or not. */
    public val isEnabled: Boolean get() = data.enabled

    /**
     * Requests the [Guild] for the onboarding.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests the [Guild] for the onboarding, returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * A [Flow] of [TopGuildChannel]s for the default channels
     */
    public val defaultChannels: Flow<TopGuildChannel> =
            supplier.getGuildChannels(guildId).filter { it.id in defaultChannelIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildOnboarding =
            GuildOnboarding(data, kord, strategy.supply(kord))
}

/**
 * Represents a prompt for a [GuildOnboarding].
 *
 * @param data The data for the prompt.
 * @param guildId The ID of the guild the prompt belongs too.
 */
public class OnboardingPrompt(
        public val data: OnboardingPromptData,
        public val guildId: Snowflake,
        override val kord: Kord
) : KordEntity {
    public override val id: Snowflake get() = data.id

    /** The [OnboardingPromptType] for this prompt. */
    public val type: OnboardingPromptType get() = data.type

    /** A [List] of [OnboardingPromptOption]s for the prompt. */
    public val options: List<OnboardingPromptOption> get() = data.options.map { OnboardingPromptOption(it, guildId, kord) }

    /** The title of the prompt. */
    public val title: String get() = data.title

    /** Whether only one option can be selected for the prompt or not. */
    public val isSingleSelect: Boolean get() = data.singleSelect

    /** Whether the prompt is required before the onboarding flow is complete. */
    public val isRequired: Boolean get() = data.required

    /**
     * Whether the prompt is present in the onboarding flow. If `false` this will only appear in the Channels & Roles tab.
     */
    public val isInOnboarding: Boolean get() = data.inOnboarding
}

/**
 * Represents an option for a [OnboardingPrompt].
 *
 * @param data The data for the prompt option
 * @param guildId The ID of the guild the onboarding belongs too
 */
public class OnboardingPromptOption(
        public val data: OnboardingPromptOptionData,
        public val guildId: Snowflake,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {
    public override val id: Snowflake get() = data.id

    /** The IDs for the channels a member is added to when the option is selected. */
    public val channelIds: List<Snowflake> get() = data.channelIds

    /** The IDs for roles assigned to a member when the option is selected. */
    public val roleIds: List<Snowflake> get() = data.roleIds

    // TODO Make a standard emoji type to allow this to not be the common type.
    /** The emoji of the option. */
    public val emoji: DiscordEmoji get() = data.emoji

    /** The title of the option. */
    public val title: String get() = data.title

    /** The description of the option. */
    public val description: String? get() = data.description

    /** A [Flow] of [TopGuildChannel]s for the [channelIds] of the option. */
    public val channels: Flow<TopGuildChannel> get() = supplier.getGuildChannels(guildId).filter { it.id in channelIds }

    /** A [Flow] of [Role]s for the [roleIds] of the option. */
    public val roles: Flow<Role> get() = supplier.getGuildRoles(guildId).filter { it.id in roleIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): OnboardingPromptOption =
            OnboardingPromptOption(data, guildId, kord, supplier)
}