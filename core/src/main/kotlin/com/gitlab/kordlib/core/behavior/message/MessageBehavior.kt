package com.gitlab.kordlib.core.behavior.message

import com.gitlab.kordlib.common.entity.User
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.Pagination
import com.gitlab.kordlib.core.`object`.ReactionEmoji
import com.gitlab.kordlib.core.`object`.builder.message.EditMessageBuilder
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.route.Position
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@ExperimentalCoroutinesApi
interface MessageBehavior : Entity {
    val channelId: Snowflake
    val channel get() = MessageChannelBehavior(channelId, kord)

    suspend fun delete() {
        kord.rest.channel.deleteMessage(channelId = channelId.value, messageId = id.value)
    }

    suspend fun getReactors(emoji: ReactionEmoji): Flow<Nothing /*User*/> {
        return Pagination.after(100, User::id) { position: Position?, size: Int ->
            kord.rest.channel.getReactions(
                    channelId = channelId.value,
                    messageId = id.value,
                    emoji = emoji.formatted,
                    limit = size,
                    position = position
            )
        }.map { TODO() }
    }

    suspend fun addReaction(emoji: ReactionEmoji) {
        kord.rest.channel.createReaction(channelId = channelId.value, messageId = id.value, emoji = emoji.formatted)
    }

    suspend fun deleteReaction(userId: Snowflake, emoji: ReactionEmoji) {
        kord.rest.channel.deleteReaction(channelId = channelId.value, messageId = id.value, userId = userId.value, emoji = emoji.formatted)
    }

    suspend fun deleteOwnReaction(emoji: ReactionEmoji) {
        kord.rest.channel.deleteOwnReaction(channelId = channelId.value, messageId = id.value, emoji = emoji.formatted)
    }

    suspend fun deleteAllReactions() {
        kord.rest.channel.deleteAllReactions(channelId = channelId.value, messageId = id.value)
    }

    companion object {
        internal operator fun invoke(channelId: Snowflake, messageId: Snowflake, kord: Kord) = object : MessageBehavior {
            override val channelId: Snowflake = channelId
            override val id: Snowflake = messageId
            override val kord: Kord = kord
        }
    }

}

suspend inline fun MessageBehavior.edit(builder: EditMessageBuilder.() -> Unit): Nothing /*Message*/ = TODO()