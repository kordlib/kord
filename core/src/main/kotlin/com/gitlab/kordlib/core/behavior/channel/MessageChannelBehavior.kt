package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.message.NewMessageBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
interface MessageChannelBehavior : ChannelBehavior {

    suspend fun createMessage(content: String): Nothing /*Message*/ = TODO()

    suspend fun getMessagesBefore(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Nothing /*Message*/> = TODO()
    suspend fun getMessagesAfter(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Nothing /*Message*/> = TODO()
    suspend fun getMessagesAround(messageId: Snowflake, limit: Int = Int.MAX_VALUE): Flow<Nothing /*Message*/> = TODO()

    suspend fun getMessage(messageId: Snowflake): Nothing /*Message*/ = TODO()

    suspend fun type() {
        kord.rest.channel.triggerTypingIndicator(id.value)
    }

    //TODO 1.3.50 add fun typeUntil(mark: ClockMark): Unit

    companion object {
        internal operator fun invoke(id: Snowflake, kord: Kord) = object : MessageChannelBehavior {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

@ExperimentalCoroutinesApi
suspend inline fun MessageChannelBehavior.createMessage(block: NewMessageBuilder.() -> Unit): Nothing = TODO()
