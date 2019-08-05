package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.`object`.builder.channel.NewInviteBuilder
import com.gitlab.kordlib.core.entity.Snowflake

/**
 * The behavior of a Discord channel associated to a [category].
 */
interface CategorizableChannelBehavior : GuildChannelBehavior {
    val categoryId: Snowflake
    val category: CategoryBehavior get() = CategoryBehavior(id = categoryId, guildId = guildId, kord = kord)

    suspend fun createInvite(builder: NewInviteBuilder): Nothing /*Invite*/ = TODO()

}