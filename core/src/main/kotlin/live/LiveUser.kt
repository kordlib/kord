package dev.kord.core.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.Entity
import dev.kord.core.entity.User
import dev.kord.core.event.Event
import dev.kord.core.event.user.UserUpdateEvent


@KordPreview
fun User.live() = LiveUser(this)

@KordPreview
class LiveUser(user: User) : AbstractLiveEntity(), Entity by user {

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