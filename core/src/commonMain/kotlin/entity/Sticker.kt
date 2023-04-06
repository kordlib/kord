package dev.kord.core.entity

import dev.kord.common.entity.MessageStickerType
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.StickerBehavior
import dev.kord.core.cache.data.StickerData
import dev.kord.core.cache.data.StickerItemData
import dev.kord.core.cache.data.StickerPackData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * A sticker image that can be used in messages.
 *
 * @param data The [StickerData] for the sticker
 */
public open class Sticker(public val data: StickerData, override val kord: Kord)  : KordEntity {

    /**
     * The id of the sticker.
     */
    override val id: Snowflake
        get() = data.id

    /**
     * The id of the pack that contains this sticker.
     */
    public val packId: Snowflake?
        get() = data.packId.value

    /**
     * The name of the sticker.
     */
    public val name: String
        get() = data.name

    /**
     * The description of the sticker.
     */
    public val description: String?
        get() = data.description

    /**
     * The tags of the sticker.
     */
    public val tags: List<String>
        get() = data.tags.value?.split(",")?.map { it.trim() }.orEmpty()

    /**
     * The type of sticker image.
     */
    public val formatType: MessageStickerType
        get() = data.formatType

    /**
     * Whether this guild sticker can be used. May be false due to a loss of boosts.
     */
    public val available: Boolean
        get() = data.available.discordBoolean

    /**
     * The standard sticker's sort order within its pack
     */
    public val sortValue: Int?
        get() = data.sortValue.value

    /**
     * The [User] that uploaded the guild sticker.
     */
    public val user: User?
        get() = data.user.unwrap { User(it, kord) }

}

/**
 * An instance of a [Sticker] specific to a give guild.
 *
 * @param data The [StickerData] for the guild sticker
 * @param kord The [Kord] instance that created this object
 */
public class GuildSticker(data: StickerData, kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier) : Sticker(data, kord), StickerBehavior {
    override val guildId: Snowflake
        get() = data.guildId.value!!


    override suspend fun asSticker(): Sticker = this

    override suspend fun asStickerOrNull(): Sticker = this

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable {
        return GuildSticker(data, kord, supplier)
    }

}

/**
 * An object for an Item of a sticker.
 *
 * @param data The [StickerItemData] for the STicker Item
 */
public class StickerItem(
    public val data: StickerItemData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordEntity, Strategizable {

    public override val id: Snowflake
        get() = data.id

    /** The name of the sticker */
    public val name: String
        get() = data.name

    /**
     * The [MessageStickerType] for the sticker
     */
    public val formatType: MessageStickerType
        get() = data.formatType

    /**
     * Gets a [Sticker] from a given [id].
     * returns `null` if the sticker cannot be found
     *
     * @return The [Sticker] or `null` if the sticker was not found
     * @throws RequestException if something went wrong during the request
     */
    public suspend fun getStickerOrNull(): Sticker? =
        supplier.getStickerOrNull(id)

    /**
     * Gets a [Sticker] from a given [id].
     *
     * @return The [Sticker] or throws an [EntityNotFoundException] if the sticker was not found
     * @throws RequestException if something went wrong during the request
     * @throws EntityNotFoundException if the sticker was null
     */
    public suspend fun getSticker(): Sticker =
        supplier.getSticker(id)


    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        StickerItem(data, kord, strategy.supply(kord))
}

/**
 * Represents a pack of standard stickers
 *
 * @param data The [StickerPackData] for the pack.
 */
public class StickerPack(public val data: StickerPackData, override val kord: Kord) : KordEntity {

    public override val id: Snowflake
        get() = data.id

    /** The name of the sticker pack */
    public val name: String get() = data.name

    /** The ID of the packs SKU */
    public val skuId: Snowflake get() = data.skuId

    /** ID of a sticker in the pack which is shown as the pack's icon */
    public val coverStickerId: Snowflake? get() = data.coverStickerId.value

    /** The sticker pack description. */
    public val description: String get() = data.description

    /** A [List] of the [Sticker]s in the pack. */
    public val stickers: List<Sticker> get() = data.stickers.map { Sticker(it, kord) }


}