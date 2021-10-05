package dev.kord.core.event.user

import dev.kord.core.Kord
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.kordCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlin.coroutines.CoroutineContext

public class UserUpdateEvent(
    public val old: User?,
    public val user: User,
    override val shard: Int,
    public val coroutineScope: CoroutineScope = kordCoroutineScope(user.kord)
) : Event, CoroutineScope by coroutineScope {
    override val kord: Kord get() = user.kord

    override fun toString(): String {
        return "UserUpdateEvent(old=$old, user=$user, shard=$shard)"
    }
}
