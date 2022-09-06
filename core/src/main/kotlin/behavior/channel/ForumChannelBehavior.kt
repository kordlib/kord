package dev.kord.core.behavior.channel

import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder
import dev.kord.rest.service.patchForumChannel
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelBehavior : ThreadParentChannelBehavior {
}

public suspend inline fun ForumChannelBehavior.edit(builder: ForumChannelModifyBuilder.() -> Unit): ForumChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.channel.patchForumChannel(id, builder)
    val data = ChannelData.from(response)
    return Channel.from(data, kord) as ForumChannel
}
