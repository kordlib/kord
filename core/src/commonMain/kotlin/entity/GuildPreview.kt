package dev.kord.core.entity

import dev.kord.common.entity.GuildFeature
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.GuildPreviewData

public class GuildPreview(
    public val data: GuildPreviewData,
    override val kord: Kord
) : KordEntity {

    override val id: Snowflake
        get() = data.id

    /**
     * The name of the guild.
     */
    public val name: String get() = data.name

    /**
     * Icon hash.
     */
    public val icon: String? get() = data.icon

    /**
     * Splash hash.
     */
    public val splash: String? get() = data.splash

    /**
     * Discovery splash hash.
     */
    public val discoverySplash: String? get() = data.discoverySplash

    /**
     * Custom guild emojis.
     */
    public val emojis: Set<GuildEmoji> get() = data.emojis.map { GuildEmoji(it, kord) }.toSet()

    /**
     * Enabled guild features.
     */
    public val features: Set<GuildFeature> get() = data.features.toSet()

    /**
     * Approximate number of members in this guild.
     */
    public val approximateMemberCount: Int get() = data.approximateMemberCount

    /**
     * Approximate number of online members in this guild.
     */
    public val approximatePresenceCount: Int get() = data.approximatePresenceCount

    /**
     * The description for the guild.
     */
    public val description: String? get() = data.description

    /**
     * Custom guild stickers.
     */
    public val stickers: Set<Sticker> get() = data.stickers.map { GuildSticker(it, kord) }.toSet()

    override fun toString(): String {
        return "GuildPreview(data=$data, kord=$kord)"
    }
}
