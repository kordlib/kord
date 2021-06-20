package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.StickerFormatType
import dev.kord.common.entity.optional.unwrap
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.GuildStickerBehavior
import dev.kord.core.cache.data.StickerData
import dev.kord.core.cache.data.UserData
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.serialization.Serializable

/**
 * A sticker image that can be used in messages.
 */
sealed class Sticker(val data: StickerData, override val kord: Kord, override val supplier: EntitySupplier) :
    KordEntity, Strategizable {


    /**
     * The id of the sticker.
     */
    override val id: Snowflake
        get() = data.id

    /**
     * The id of the pack that contains this sticker.
     */
    val packId: Snowflake?
        get() = data.packId.value

    /**
     * The name of the sticker.
     */
    val name: String
        get() = data.name

    /**
     * The description of the sticker.
     */
    val description: String
        get() = data.description

    /**
     * The tags of the sticker.
     */
    val tags: List<String>
        get() = data.tags.split(",").map { it.trim() }

    /**
     * The sticker image hash asset (currently private).
     */
    val asset: String
        get() = data.asset

    /**
     * The type of sticker image.
     */
    val formatType: StickerFormatType
        get() = data.formatType

    val available: Boolean?
        get() = data.available.value

    val user: User?
        get() = data.user.unwrap {
            val data = UserData.from(it)

            User(data, kord, supplier)
        }

    val sortValue: Int?
        get() = data.sortValue.value

    companion object {
        fun from(data: StickerData, kord: Kord, supplier: EntitySupplier = kord.defaultSupplier): Sticker {
            return if (data.guildId.value != null) {
                GuildSticker(data, kord, supplier)
            } else {
                DiscordSticker(data, kord, supplier)
            }
        }
    }
}

class DiscordSticker(data: StickerData, kord: Kord, supplier: EntitySupplier = kord.defaultSupplier) : Sticker(data, kord, supplier) {
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        DiscordSticker(data, kord, strategy.supply(kord))
}

class GuildSticker(data: StickerData, kord: Kord, supplier: EntitySupplier = kord.defaultSupplier) : Sticker(data, kord, supplier), GuildStickerBehavior {
    override val guildId: Snowflake get() = data.guildId.value!!

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Strategizable =
        GuildSticker(data, kord, strategy.supply(kord))
}
