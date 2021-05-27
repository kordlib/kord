package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.user.UserUpdateEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.job

@KordPreview
fun User.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = kord.coroutineContext.job
) = LiveUser(this, dispatcher, parent)

@KordPreview
inline fun User.live(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = kord.coroutineContext.job,
    block: LiveUser.() -> Unit
) = this.live(dispatcher, parent).apply(block)

@KordPreview
fun LiveUser.onUpdate(block: suspend (UserUpdateEvent) -> Unit) = on(consumer = block)

@KordPreview
class LiveUser(
    user: User,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    parent: Job? = user.kord.coroutineContext.job
) : AbstractLiveKordEntity(user.kord, dispatcher, parent), KordEntity {

    override val id: Snowflake
        get() = user.id

    var user: User = user
        private set

    override fun filter(event: Event) = when (event) {
        is UserUpdateEvent -> user.id == event.user.id
        else -> false
    }

    override fun update(event: Event) = when (event) {
        is UserUpdateEvent -> user = event.user
        else -> Unit
    }

}
