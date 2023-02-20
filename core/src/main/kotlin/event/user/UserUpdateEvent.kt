package dev.kord.core.event.user

import dev.kord.core.Kord
import dev.kord.core.entity.User
import dev.kord.core.event.Event

/**
 * The event dispatched when a [User] is updated.
 *
 * See [User update](https://discord.com/developers/docs/topics/gateway-events#user-update)
 *
 * @param old The old [User] that triggered the event. It may be `null` if it was not stored in the cache
 * @param user The [User] that triggered the event.
 */
public class UserUpdateEvent(
    public val old: User?,
    public val user: User,
    override val shard: Int,
    override val customContext: Any?,
) : Event {
    override val kord: Kord get() = user.kord

    override fun toString(): String {
        return "UserUpdateEvent(old=$old, user=$user, shard=$shard)"
    }
}
