package com.gitlab.kordlib.core.entity.channel

import com.gitlab.kordlib.core.behavior.MessageBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.entity.Message
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.toInstant
import com.gitlab.kordlib.core.toSnowflakeOrNull
import java.time.Instant

/**
 * An instance of a Discord channel that can use messages.
 */
interface MessageChannel : Channel, MessageChannelBehavior {

    /**
     * The id of the last message sent to this channel, if present.
     */
    val lastMessageId: Snowflake? get() = data.lastMessageId.toSnowflakeOrNull()

    /**
     * The behavior of the last message sent to this channel, if present.
     */
    val lastMessage: MessageBehavior? get() = lastMessageId?.let { MessageBehavior(id, it, kord) }

    /**
     * The timestamp of the last pin
     */
    val lastPintTimeStamp: Instant? get() =
        data.lastPinTimestamp?.toInstant()

    /**
     * Requests to get the last message sent to this channel, if present.
     */
    suspend fun getLastMessage(): Message? {
        val messageId = lastMessageId ?: return null

        return kord.getMessage(id, messageId)
    }

}