package com.gitlab.kordlib.core.event.user

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.Event

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