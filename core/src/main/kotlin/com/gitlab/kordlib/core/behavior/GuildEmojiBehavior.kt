package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.EmojiData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.GuildEmoji
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.guild.EmojiModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import java.util.*

/**
 * The behavior of a [Discord Emoij](https://discord.com/developers/docs/resources/emoji).
 */
interface GuildEmojiBehavior : Entity, Strategizable {

    /**
     * The id of the guild this emojis is part of.
     */
    val guildId: Snowflake

    /**
     * The behavior of the guild this emoji is part of.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to delete this emoji.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun delete() {
        kord.rest.emoji.deleteEmoji(guildId = guildId.value, emojiId = id.value)
    }

    /**
     * Returns a new [GuildEmojiBehavior] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildEmojiBehavior =
            GuildEmojiBehavior(guildId = guildId, id = id, kord = kord, strategy = strategy)

    companion object {
        internal operator fun invoke(
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

            override fun equals(other: Any?): Boolean = when(other) {
                is GuildEmojiBehavior -> other.id == id
                else -> false
            }
        }
    }
}

/**
 * Requests to edit this emoji.
 *
 * @return The edited [GuildEmoji].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
suspend inline fun GuildEmojiBehavior.edit(builder: EmojiModifyBuilder.() -> Unit): GuildEmoji {
    val response = kord.rest.emoji.modifyEmoji(guildId.value, id.value, builder)
    val data = EmojiData.from(guildId = guildId.value, id = id.value, entity = response)

    return GuildEmoji(data, kord)
}