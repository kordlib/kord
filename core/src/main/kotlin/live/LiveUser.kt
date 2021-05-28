package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.user.UserUpdateEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job

@KordPreview
fun User.live(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob(kord.coroutineContext.job))
) = LiveUser(this, coroutineScope)

@KordPreview
inline fun User.live(
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob(kord.coroutineContext.job)),
    block: LiveUser.() -> Unit
) = this.live(coroutineScope).apply(block)

@KordPreview
fun LiveUser.onUpdate(scope: CoroutineScope = this, block: suspend (UserUpdateEvent) -> Unit) =
    on(scope = scope, consumer = block)

@KordPreview
class LiveUser(
    user: User,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob(user.kord.coroutineContext.job))
) : AbstractLiveKordEntity(user.kord, coroutineScope), KordEntity {

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
