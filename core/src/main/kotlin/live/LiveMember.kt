package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Member
import dev.kord.core.event.Event
import dev.kord.core.event.guild.BanAddEvent
import dev.kord.core.event.guild.GuildDeleteEvent
import dev.kord.core.event.guild.MemberLeaveEvent
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.live.channel.LiveChannel
import dev.kord.core.live.exception.LiveCancellationException
import kotlinx.coroutines.*

/**
 * Returns a [LiveMember] for a given [Member].
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveChannel] with
 * @return the created [LiveMember]
 */
@KordPreview
public fun Member.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveMember = LiveMember(this, coroutineScope)

/**
 * Returns a [LiveMember] for a given [Member] with configuration.
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveChannel] with
 * @param block The [LiveMember] configuration
 * @return the created [LiveMember]
 */
@KordPreview
public inline fun Member.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveMember.() -> Unit
): LiveMember = this.live(coroutineScope).apply(block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveMember.onUpdate(scope: CoroutineScope = this, block: suspend (MemberUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * A [AbstractLiveKordEntity] for a [Member]
 *
 * @property member The [Member] to get the live entity for
 * @property coroutineContext The [CoroutineScope] to create the live object with
 */
@KordPreview
public class LiveMember(
    member: Member,
    coroutineScope: CoroutineScope = member.kord + SupervisorJob(member.kord.coroutineContext.job)
) : AbstractLiveKordEntity(member.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = member.id

    /**
     * The [Member] to create the live entity for
     */
    public var member: Member = member
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is MemberLeaveEvent -> member.id == event.user.id
        is MemberUpdateEvent -> member.id == event.member.id
        is BanAddEvent -> member.id == event.user.id
        is GuildDeleteEvent -> member.guildId == event.guildId
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is MemberLeaveEvent -> shutDown(LiveCancellationException(event, "The member has left"))
        is BanAddEvent -> shutDown(LiveCancellationException(event, "The member is banned"))
        is GuildDeleteEvent -> shutDown(LiveCancellationException(event, "The guild is deleted"))
        is MemberUpdateEvent -> member = event.member

        else -> Unit
    }
}
