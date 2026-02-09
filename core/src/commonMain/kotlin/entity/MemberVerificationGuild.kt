package dev.kord.core.entity

import dev.kord.common.annotation.DiscordAPIPreview
import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.VerificationLevel
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.MemberVerificationGuildData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier

@DiscordAPIPreview
public class MemberVerificationGuild(
    public val data: MemberVerificationGuildData,
    override val kord: Kord,
    public val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity {
    override val id: Snowflake get() = data.id

    /**
     * Requests the [Guild] with the given [id].
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(id)

    /**
     * Requests the [Guild] with the given [id], returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    /**
     * The name of the guild (2-100 characters)
     */
    public val name: String get() = data.name

    /**
     * The guilds icon hash, if present
     */
    public val iconHash: String? get() = data.icon

    public val icon: Asset? get() = iconHash?.let { Asset.guildIcon(id, it, kord) }

    /**
     * The description for the guild (max 300 characters)
     */
    public val description: String? get() = data.description

    /**
     * The splash hash, if present.
     */
    public val splashHash: String? get() = data.splash

    public val splash: Asset? get() = splashHash?.let { Asset.guildSplash(id, it, kord) }

    /**
     * The hash of the discovery splash, if present.
     */
    public val discoverySplashHash: String? get() = data.discoverySplash

    public val discoverySplash: Asset? get() = discoverySplashHash?.let { Asset.guildDiscoverySplash(id, it, kord) }

    /**
     * The hash of the home header, if present.
     */
    public val homeHeaderHash: String? get() = data.homeHeader

    public val homeHeader: Asset? get() = homeHeaderHash?.let { Asset.guildHomeHeader(id, it, kord) }

    /**
     * The guilds [VerificationLevel]
     */
    public val verificationLevel: VerificationLevel get() = data.verificationLevel

    /**
     * Enabled [GuildFeature]s
     */
    public val features: List<GuildFeature> get() = data.features

    /**
     * The ID's of custom guild emojis
     */
    public val emojisIds: Set<Snowflake> get() = data.emojis.toSet()

    /**
     * Requests the [GuildEmoji] with the [emojiId] in the [Guild] wit the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the emoji.
     * @throws EntityNotFoundException if the emoji was null.
     */
    public suspend fun getEmoji(emojiId: Snowflake): GuildEmoji =
        supplier.getEmoji(guildId = id, emojiId = emojiId)

    /**
     * Requests the [GuildEmoji] with the [emojiId] in the [Guild] wit the given [guildId],
     * returns null when the emoji isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the emoji.
     */
    public suspend fun getEmojiOrNull(emojiId: Snowflake): GuildEmoji? =
        supplier.getEmojiOrNull(guildId = id, emojiId = emojiId)

    /**
     * Approximate number of total members in the guild
     */
    public val approximateMemberCount: Int get() = data.approximateMemberCount

    /**
     * Approximate number of non-offline members in the guild
     */
    public val approximatePresenceCount: Int get() = data.approximatePresenceCount
}