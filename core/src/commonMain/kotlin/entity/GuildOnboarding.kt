package dev.kord.core.entity

import dev.kord.common.entity.*
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.RoleBehavior
import dev.kord.core.behavior.channel.TopGuildChannelBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter

/**
 * Represents the [onboarding](https://support.discord.com/hc/en-us/articles/11074987197975-Community-Onboarding-FAQ)
 * flow for a [Guild].
 *
 * @param data The [DiscordGuildOnboarding] for the onbaording
 */
public class GuildOnboarding(
    public val data: DiscordGuildOnboarding,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {
    /** The ID of the [Guild] this onboarding is part of. */
    public val guildId: Snowflake get() = data.guildId

    /** The behavior of the [Guild] this onboarding is part of. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** The [Prompt]s shown during onboarding and in customize community. */
    public val prompts: List<Prompt> get() = data.prompts.map { Prompt(it, guildId, kord) }

    /** The IDs of the [channels][TopGuildChannel] that [Member]s get opted into automatically. */
    public val defaultChannelIds: List<Snowflake> get() = data.defaultChannelIds

    /** The behaviors of the [channels][TopGuildChannel] that [Member]s get opted into automatically. */
    public val defaultChannelBehaviors: List<TopGuildChannelBehavior>
        get() = defaultChannelIds.map { channelId -> TopGuildChannelBehavior(guildId, id = channelId, kord) }

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
    public val defaultChannels: Flow<TopGuildChannel>
        get() {
            val ids = defaultChannelIds
            return if (ids.isEmpty()) emptyFlow() else supplier.getGuildChannels(guildId).filter { it.id in ids }
        }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildOnboarding =
        GuildOnboarding(data, kord, strategy.supply(kord))

    override fun toString(): String = "GuildOnboarding(data=$data, kord=$kord, supplier=$supplier)"


    /**
     * Represents a prompt for a [GuildOnboarding].
     *
     * @property guildId The ID of the [Guild] the [onboarding][GuildOnboarding] is part of.
     */
    public class Prompt(
        public val data: DiscordOnboardingPrompt,
        public val guildId: Snowflake,
        override val kord: Kord,
    ) : KordEntity {
        override val id: Snowflake get() = data.id

        /** The [type][OnboardingPromptType] of this prompt. */
        public val type: OnboardingPromptType get() = data.type

        /** The [Option]s available within this prompt. */
        public val options: List<Option> get() = data.options.map { Option(it, guildId, kord) }

        /** The title of this prompt. */
        public val title: String get() = data.title

        /** Indicates whether users are limited to selecting one option for this prompt. */
        public val isSingleSelect: Boolean get() = data.singleSelect

        /** Indicates whether this prompt is required before a user completes the [onboarding][GuildOnboarding] flow. */
        public val isRequired: Boolean get() = data.required

        /**
         * Indicates whether this prompt is present in the [onboarding][GuildOnboarding] flow. If `false`, this prompt
         * will only appear in the Channels & Roles tab.
         */
        public val isInOnboarding: Boolean get() = data.inOnboarding

        override fun equals(other: Any?): Boolean =
            other is Prompt && this.id == other.id && this.guildId == other.guildId

        override fun hashCode(): Int = hash(id, guildId)
        override fun toString(): String = "GuildOnboarding.Prompt(data=$data, guildId=$guildId, kord=$kord)"


        /**
         * Represents an option for a [Prompt].
         *
         * @property guildId The ID of the [Guild] the [onboarding][GuildOnboarding] is part of.
         */
        public class Option(
            public val data: DiscordOnboardingPromptOption,
            public val guildId: Snowflake,
            override val kord: Kord,
            override val supplier: EntitySupplier = kord.defaultSupplier,
        ) : KordEntity, Strategizable {
            override val id: Snowflake get() = data.id

            /** The IDs for the [channels][TopGuildChannel] a [Member] is added to when this option is selected. */
            public val channelIds: List<Snowflake> get() = data.channelIds

            /**
             * The behaviors for the [channels][TopGuildChannel] a [Member] is added to when this option is selected.
             */
            public val channelBehaviors: List<TopGuildChannelBehavior>
                get() = channelIds.map { channelId -> TopGuildChannelBehavior(guildId, id = channelId, kord) }

            /** The IDs for the [Role]s assigned to a [Member] when this option is selected. */
            public val roleIds: List<Snowflake> get() = data.roleIds

            /** The behaviors for the [Role]s assigned to a [Member] when this option is selected. */
            public val roleBehaviors: List<RoleBehavior>
                get() = roleIds.map { roleId -> RoleBehavior(guildId, id = roleId, kord) }

            /** The [Emoji] of this option. */
            public val emoji: Emoji?
                get() {
                    val emoji = data.emoji
                    return emoji.id?.let { emojiId -> GuildEmoji(emoji.toData(guildId, emojiId), kord) }
                        ?: emoji.name?.let(::StandardEmoji)
                }

            /** The title of this option. */
            public val title: String get() = data.title

            /** The description of this option. */
            public val description: String? get() = data.description

            /**
             * Requests the [channels][TopGuildChannel] a [Member] is added to when this option is selected.
             *
             * The returned [Flow] is lazily executed, any [RequestException] will be thrown on
             * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators)
             * instead.
             */
            public val channels: Flow<TopGuildChannel>
                get() {
                    val ids = channelIds
                    return if (ids.isEmpty()) {
                        emptyFlow()
                    } else {
                        supplier.getGuildChannels(guildId).filter { it.id in ids }
                    }
                }

            /**
             * Requests the [Role]s assigned to a [Member] when this option is selected.
             *
             * The returned [Flow] is lazily executed, any [RequestException] will be thrown on
             * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators)
             * instead.
             */
            public val roles: Flow<Role>
                get() {
                    val ids = roleIds
                    return if (ids.isEmpty()) emptyFlow() else supplier.getGuildRoles(guildId).filter { it.id in ids }
                }

            override fun withStrategy(strategy: EntitySupplyStrategy<*>): Option =
                Option(data, guildId, kord, strategy.supply(kord))

            override fun equals(other: Any?): Boolean =
                other is Option && this.id == other.id && this.guildId == other.guildId

            override fun hashCode(): Int = hash(id, guildId)
            override fun toString(): String =
                "GuildOnboarding.Prompt.Option(data=$data, guildId=$guildId, kord=$kord, supplier=$supplier)"
        }
    }
}
