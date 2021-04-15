package dev.kord.core.event.user

import dev.kord.core.Kord
import dev.kord.core.entity.User
import dev.kord.core.event.Event

class UserUpdateEvent(
    val old: User?,
    val user: User,
    override val shard: Int
) : Event {
    override val kord: Kord get() = user.kord

    override fun toString(): String {
        return "UserUpdateEvent(old=$old, user=$user, shard=$shard)"
    }
}