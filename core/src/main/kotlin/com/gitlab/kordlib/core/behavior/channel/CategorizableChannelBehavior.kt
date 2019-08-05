package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.`object`.builder.channel.UpdateGuildChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake

interface CategorizableChannelBehavior<T : UpdateGuildChannelBuilder> : GuildChannelBehavior<T> {
    val categoryId: Snowflake
    val category: CategoryBehavior get() = CategoryBehavior(id = categoryId, guilId = guildId, kord = kord)


}