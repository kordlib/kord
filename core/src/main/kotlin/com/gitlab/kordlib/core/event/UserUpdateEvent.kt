package com.gitlab.kordlib.core.event

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.entity.User

class UserUpdateEvent(
        val old: User?,
        val user: User
) : Event {
    override val kord: Kord get() = user.kord
}