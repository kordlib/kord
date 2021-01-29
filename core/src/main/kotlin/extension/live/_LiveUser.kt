package dev.kord.core.extension.live

import dev.kord.common.annotation.KordPreview
import dev.kord.core.entity.User
import dev.kord.core.event.user.UserUpdateEvent
import dev.kord.core.live.LiveUser
import dev.kord.core.live.live
import dev.kord.core.live.on

@KordPreview
inline fun User.live(block: LiveUser.() -> Unit) = this.live().apply(block)

@KordPreview
fun LiveUser.update(block: suspend (UserUpdateEvent) -> Unit) = on(consumer = block)
