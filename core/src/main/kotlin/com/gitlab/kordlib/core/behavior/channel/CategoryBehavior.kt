package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateCategoryBuilder
import com.gitlab.kordlib.core.behavior.guild.GuildBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake

interface CategoryBehavior : Entity {
    val guildId: Snowflake
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    companion object {
            internal operator fun invoke(guilId: Snowflake, id: Snowflake, kord: Kord) : CategoryBehavior = object : CategoryBehavior {
                override val guildId: Snowflake = guilId
                override val id: Snowflake = id
                override val kord: Kord = kord
            }
    }
}

suspend fun CategoryBehavior.edit(builder: UpdateCategoryBuilder.() -> Unit): Nothing /*Category*/ = TODO()