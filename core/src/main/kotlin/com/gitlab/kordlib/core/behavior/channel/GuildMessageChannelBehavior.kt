package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateGuildChannelBuilder
import com.gitlab.kordlib.core.`object`.builder.webhook.NewWebhookBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.BulkDeleteRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@ExperimentalCoroutinesApi
interface GuildMessageChannelBehavior : CategorizableChannelBehavior, MessageChannelBehavior {

    suspend fun bulkDelete(messages: Iterable<Snowflake>) {
        val request = BulkDeleteRequest(messages.map { it.value })
        kord.rest.channel.bulkDelete(id.value, request)
    }

    suspend fun getPinnedMessage(): Flow<Nothing /*Message*/> = TODO()

    companion object {
        internal operator fun invoke(guildId: Snowflake, categoryId: Snowflake, id: Snowflake, kord: Kord) = object : GuildMessageChannelBehavior {
            override val guildId: Snowflake = guildId
            override val categoryId: Snowflake = categoryId
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }
}

suspend inline fun GuildMessageChannelBehavior.createWebhook(builder: NewWebhookBuilder.() -> Unit): Nothing = TODO()
