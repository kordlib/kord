package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.threads.ThreadParentChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.ForumChannel
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.rest.builder.channel.ForumChannelModifyBuilder
import dev.kord.rest.builder.channel.ForumTagBuilder
import dev.kord.rest.builder.channel.ModifyForumTagBuilder
import dev.kord.rest.builder.channel.thread.StartForumThreadBuilder
import dev.kord.rest.service.patchForumChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.datetime.Instant
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelBehavior : ThreadParentChannelBehavior {
    override val activeThreads: Flow<TextChannelThread>
        get() = super.activeThreads.filterIsInstance()

    override fun getPublicArchivedThreads(before: Instant?, limit: Int?): Flow<TextChannelThread> {
        return super.getPublicArchivedThreads(before, limit).filterIsInstance()
    }
    public suspend fun createTag(name: String, builder: ForumTagBuilder.() -> Unit = {}): ForumChannel {
        val request = kord.rest.channel.createForumTag(id, name, builder)
        val data = ChannelData.from(request)

        return Channel.from(data, kord) as ForumChannel
    }

    public suspend fun deleteTag(tagId: Snowflake, reason: String? = null): ForumChannel {
        val request = kord.rest.channel.deleteForumTag(id, tagId, reason)
        val data = ChannelData.from(request)

        return Channel.from(data, kord) as ForumChannel
    }

    public suspend fun editTag(tagId: Snowflake, builder: ModifyForumTagBuilder.() -> Unit): ForumChannel {
        val request = kord.rest.channel.editForumTag(id, tagId, builder)
        val data = ChannelData.from(request)

        return Channel.from(data, kord) as ForumChannel
    }

    public suspend fun startPublicThread(
        name: String,
        builder: StartForumThreadBuilder.() -> Unit = {}
    ): TextChannelThread {
        return unsafeStartThread(name, builder)
    }
}

internal suspend fun ThreadParentChannelBehavior.unsafeStartThread(
    name: String,
    builder: StartForumThreadBuilder.() -> Unit
): TextChannelThread {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.startForumThread(id, name, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as TextChannelThread
}

public suspend inline fun ForumChannelBehavior.edit(builder: ForumChannelModifyBuilder.() -> Unit): ForumChannel {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }

    val response = kord.rest.channel.patchForumChannel(id, builder)
    val data = ChannelData.from(response)

    return Channel.from(data, kord) as ForumChannel
}
