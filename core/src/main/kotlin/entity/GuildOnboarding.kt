package dev.kord.core.entity

import dev.kord.common.entity.*
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import java.util.*

/**
 * Represents an instance of a Guild Onboarding
 *
 * @param data The data for the onboarding.
 */
public class GuildOnboarding(
        public val data: DiscordGuildOnboarding,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {
    /** The ID of the guild the onboarding is for. */
    public val guildId: Snowflake get() = data.guildId

    /** The [GuildBehavior] for the onboarding. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** A list of [Prompt]s for the onboarding. */
    public val prompts: List<Prompt> get() = data.prompts.map { Prompt(it, guildId, kord) }

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

    /**
     * Represents a prompt for a [GuildOnboarding].
     *
     * @param data The data for the prompt.
     * @param guildId The ID of the guild the prompt belongs too.
     */
    public class Prompt(
            public val data: DiscordOnboardingPrompt,
            public val guildId: Snowflake,
            override val kord: Kord
    ) : KordEntity {
        public override val id: Snowflake get() = data.id

        /** The [OnboardingPromptType] for this prompt. */
        public val type: OnboardingPromptType get() = data.type

        /** A [List] of [Option]s for the prompt. */
        public val options: List<Option> get() = data.options.map { Option(it, guildId, kord) }

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

        override fun toString(): String =
                "OnboardingPrompt(data=$data, guildId=$guildId, kord=$kord)"

        override fun equals(other: Any?): Boolean = when (other) {
            is Prompt -> other.id == id && other.guildId == guildId
            else -> super.equals(other)
        }

        override fun hashCode(): Int = Objects.hash(id, guildId)

        /**
         * Represents an option for a [Prompt].
         *
         * @param data The data for the prompt option
         * @param guildId The ID of the guild the onboarding belongs too
         */
        public class Option(
                public val data: DiscordOnboardingPromptOption,
                public val guildId: Snowflake,
                override val kord: Kord,
                override val supplier: EntitySupplier = kord.defaultSupplier
        ) : KordEntity, Strategizable {
            public override val id: Snowflake get() = data.id

            /** The IDs for the channels a member is added to when the option is selected. */
            public val channelIds: List<Snowflake> get() = data.channelIds

            /** The IDs for roles assigned to a member when the option is selected. */
            public val roleIds: List<Snowflake> get() = data.roleIds

            /** The emoji of the option. */
            public val emoji: Emoji
                get() = when (data.emoji.id) {
                    null -> StandardEmoji(data.emoji.name!!)
                    else -> GuildEmoji(data.emoji.toData(guildId, data.emoji.id!!), kord)
                }

            /** The title of the option. */
            public val title: String get() = data.title

            /** The description of the option. */
            public val description: String? get() = data.description

            /** A [Flow] of [TopGuildChannel]s for the [channelIds] of the option. */
            public val channels: Flow<TopGuildChannel> get() = supplier.getGuildChannels(guildId).filter { it.id in channelIds }

            /** A [Flow] of [Role]s for the [roleIds] of the option. */
            public val roles: Flow<Role> get() = supplier.getGuildRoles(guildId).filter { it.id in roleIds }

            override fun withStrategy(strategy: EntitySupplyStrategy<*>): Option =
                    Option(data, guildId, kord, supplier)

            override fun toString(): String =
                    "OnboardingPromptOption(data=$data, guildId=$guildId, kord=$kord, supplier=$supplier)"

            override fun equals(other: Any?): Boolean = when (other) {
                is Option -> other.id == id && other.guildId == guildId
                else -> super.equals(other)
            }

            override fun hashCode(): Int = Objects.hash(id, guildId)
        }
    }
}