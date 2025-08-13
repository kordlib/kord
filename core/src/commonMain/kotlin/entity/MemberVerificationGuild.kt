package dev.kord.core.entity

import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.VerificationLevel
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.MemberVerificationGuildData
import dev.kord.core.supplier.EntitySupplier

public class MemberVerificationGuild(
    public val data: MemberVerificationGuildData,
    override val kord: Kord,
    public val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity {
    override val id: Snowflake get() = data.id

    public suspend fun getGuild(): Guild = supplier.getGuild(id)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(id)

    public val name: String get() = data.name

    public val iconHash: String? get() = data.icon

    public val icon: Asset? get() = iconHash?.let { Asset.guildIcon(id, it, kord) }

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

    public val verificationLevel: VerificationLevel get() = data.verificationLevel

    public val features: List<GuildFeature> get() = data.features

    public val emojisIds: Set<Snowflake> get() = data.emojis.toSet()

    public suspend fun getEmoji(emojiId: Snowflake): GuildEmoji =
        supplier.getEmoji(guildId = id, emojiId = emojiId)

    public suspend fun getEmojiOrNull(emojiId: Snowflake): GuildEmoji? =
        supplier.getEmojiOrNull(guildId = id, emojiId = emojiId)

    public val approximateMemberCount: Int get() = data.approximateMemberCount

    public val approximatePresenceCount: Int get() = data.approximatePresenceCount
}