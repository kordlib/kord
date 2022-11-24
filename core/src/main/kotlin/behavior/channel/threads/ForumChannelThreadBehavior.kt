package dev.kord.core.behavior.channel.threads

import dev.kord.core.cache.data.toData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.thread.ForumChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.rest.builder.channel.thread.ForumThreadModifyBuilder
import dev.kord.rest.builder.channel.thread.ThreadModifyBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public interface ForumChannelThreadBehavior : ThreadChannelBehavior

/**
 * * Editing a thread to set [archived][ThreadChannel.isArchived] to false only requires the current user to be in the thread.
 * * If [locked][ThreadChannel.isLocked] is true, then the user must have [Manage Threads][dev.kord.common.entity.Permission.ManageThreads]
 * * Editing a thread to change the
 * [name][ThreadModifyBuilder.name],
 * [archived][ThreadModifyBuilder.archived],
 * [autoArchiveDuration][ThreadModifyBuilder.autoArchiveDuration] fields
 * requires [Manage Threads][dev.kord.common.entity.Permission.ManageThreads] or that the current user is the thread creator.
 */
public suspend inline fun ForumChannelThreadBehavior.edit(builder: ForumThreadModifyBuilder.() -> Unit): ForumChannelThread {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val appliedBuilder = ForumThreadModifyBuilder().apply(builder)
    val patchedChannel = kord.rest.channel.patchThread(id, appliedBuilder.toRequest(), appliedBuilder.reason)
    return Channel.from(patchedChannel.toData(), kord) as ForumChannelThread
}