package com.gitlab.kordlib.core.behavior.guild

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.guild.UpdateGuildEmojiBuilder
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

interface GuildEmojiBehavior : Entity {
    val guildId: Snowflake
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)


    suspend fun delete() {
        kord.rest.emoji.deleteEmoji(guildId = guildId.value, emojiId = id.value)
    }

    suspend fun edit(builder: UpdateGuildEmojiBuilder) : Nothing /*GuildEmoji*/ = TODO()

    companion object {
        internal operator fun invoke(id: Snowflake, guildId: Snowflake, kord: Kord): GuildEmojiBehavior = object : GuildEmojiBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

suspend inline fun GuildEmojiBehavior.edit(builder: UpdateGuildEmojiBuilder.() -> Unit): Nothing = edit(UpdateGuildEmojiBuilder().apply(builder))