package dev.kord.core.behavior

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.EmojiData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.GuildEmoji
import dev.kord.core.entity.Strategizable
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.guild.EmojiModifyBuilder
import dev.kord.rest.request.RestRequestException
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Discord Emoij](https://discord.com/developers/docs/resources/emoji).
 */
public interface GuildEmojiBehavior : KordEntity, Strategizable {

    /**
     * The id of the guild this emojis is part of.
     */
    public val guildId: Snowflake

    /**
     * The behavior of the guild this emoji is part of.
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to delete this emoji.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun delete(reason: String? = null) {
        kord.rest.emoji.deleteEmoji(guildId = guildId, emojiId = id, reason = reason)
    }

    /**
     * Returns a new [GuildEmojiBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildEmojiBehavior =
        GuildEmojiBehavior(guildId = guildId, id = id, kord = kord, strategy = strategy)
}

internal fun GuildEmojiBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): GuildEmojiBehavior = object : GuildEmojiBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildEmojiBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "GuildEmoijBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to edit this emoji.
 *
 * @return The edited [GuildEmoji].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun GuildEmojiBehavior.edit(builder: EmojiModifyBuilder.() -> Unit): GuildEmoji {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.emoji.modifyEmoji(guildId, id, builder)
    val data = EmojiData.from(guildId = guildId, id = id, entity = response)

    return GuildEmoji(data, kord)
}
