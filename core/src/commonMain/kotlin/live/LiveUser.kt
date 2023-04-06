package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.user.UserUpdateEvent
import kotlinx.coroutines.*

/**
 * Returns a [LiveUser] for a given [User].
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveUser] with
 * @return the created [LiveUser]
 */
@KordPreview
public fun User.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job)
): LiveUser = LiveUser(this, coroutineScope)

/**
 * Returns a [LiveUser] for a given [User] with configuration.
 *
 * @param coroutineScope The [CoroutineScope] to create the [LiveUser] with
 * @param block The [LiveUser] configuration
 * @return the created [LiveUser]
 */
@KordPreview
public inline fun User.live(
    coroutineScope: CoroutineScope = kord + SupervisorJob(kord.coroutineContext.job),
    block: LiveUser.() -> Unit
): LiveUser = this.live(coroutineScope).apply(block)

/**
 * Invokes the consumer for this entity with [block] for the given [CoroutineScope]
 *
 * @param scope The [CoroutineScope] to invoke the consumer with
 * @param block The configuration for the consumer
 */
@KordPreview
public fun LiveUser.onUpdate(scope: CoroutineScope = this, block: suspend (UserUpdateEvent) -> Unit): Job =
    on(scope = scope, consumer = block)

/**
 * A [AbstractLiveKordEntity] for a [User]
 *
 * @property user The [User] to get the live entity for
 * @property coroutineContext The [CoroutineScope] to create the live object with
 */
@KordPreview
public class LiveUser(
    user: User,
    coroutineScope: CoroutineScope = user.kord + SupervisorJob(user.kord.coroutineContext.job)
) : AbstractLiveKordEntity(user.kord, coroutineScope), KordEntity {

    override val id: Snowflake
        get() = user.id

    public var user: User = user
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is UserUpdateEvent -> user.id == event.user.id
        else -> false
    }

    override fun update(event: Event): Unit = when (event) {
        is UserUpdateEvent -> user = event.user
        else -> Unit
    }

}
