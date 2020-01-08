package com.gitlab.kordlib.core.behavior

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.rest.builder.guild.EmojiModifyBuilder
import com.gitlab.kordlib.core.cache.data.EmojiData
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.GuildEmoji
import com.gitlab.kordlib.common.entity.Snowflake

/**
 * The behavior of a [Discord Emoij](https://discordapp.com/developers/docs/resources/emoji).
 */
interface GuildEmojiBehavior : Entity {
    val guildId: Snowflake
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to delete this emoji.
     */
    suspend fun delete() {
        kord.rest.emoji.deleteEmoji(guildId = guildId.value, emojiId = id.value)
    }

    companion object {
        internal operator fun invoke(guildId: Snowflake, id: Snowflake, kord: Kord): GuildEmojiBehavior = object : GuildEmojiBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

/**
 * Requests to edit this emoji.
 *
 * @return The edited [GuildEmoji].
 */
@Suppress("NAME_SHADOWING")
suspend inline fun GuildEmojiBehavior.edit(builder: EmojiModifyBuilder.() -> Unit): GuildEmoji {
    val builder = EmojiModifyBuilder().apply(builder)
    val reason = builder.reason
    val request = builder.toRequest()

    val response = kord.rest.emoji.modifyEmoji(guildId.value, id.value, request, reason)
    val data = EmojiData.from(id.value, response)

    return GuildEmoji(data, guildId, kord)
}