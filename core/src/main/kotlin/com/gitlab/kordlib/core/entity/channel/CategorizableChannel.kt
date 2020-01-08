package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.behavior.channel.CategoryBehavior
import com.gitlab.kordlib.rest.builder.channel.InviteCreateBuilder
import com.gitlab.kordlib.core.cache.data.InviteData
import com.gitlab.kordlib.core.entity.Invite
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.toSnowflakeOrNull

/**
 * An instance of a Discord channel associated to a [category].
 */
interface CategorizableChannel: GuildChannel {

    /**
     * The id of the [category] this channel belongs to, if any.
     */
    val categoryId: Snowflake?
        get() = data.parentId.toSnowflakeOrNull()

    /**
     * The category behavior this channel belongs to, if any.
     */
    val category: CategoryBehavior?
        get() = when (val categoryId = categoryId) {
            null -> null
            else -> CategoryBehavior(id = categoryId, guildId = guildId, kord = kord)
        }

    override suspend fun asChannel(): CategorizableChannel {
        return super.asChannel() as CategorizableChannel
    }

    /**
     * Request to create an invite for this channel.
     *
     * @return the created [Invite].
     */
    suspend fun createInvite(builder: InviteCreateBuilder.() -> Unit): Invite {
            val response = kord.rest.channel.createInvite(id.value, builder)
        val data = InviteData.from(response)

        return Invite(data, kord)
    }


}