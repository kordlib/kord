package com.gitlab.kordlib.core.live

import com.gitlab.kordlib.core.entity.Entity
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.*
import com.gitlab.kordlib.core.event.channel.ChannelCreateEvent
import com.gitlab.kordlib.core.event.channel.ChannelDeleteEvent
import com.gitlab.kordlib.core.event.channel.ChannelUpdateEvent
import com.gitlab.kordlib.core.event.guild.*
import com.gitlab.kordlib.core.event.message.*
import com.gitlab.kordlib.core.event.role.RoleCreateEvent
import com.gitlab.kordlib.core.event.role.RoleDeleteEvent
import com.gitlab.kordlib.core.event.role.RoleUpdateEvent

//TODO
class LiveUser(user: User) : AbstractLiveEntity(), Entity by user {

    var user: User = user
        private set

    override fun filter(event: Event): Boolean = when (event) {
        is UserUpdateEvent -> event.user.id == user.id
        is UnbanEvent -> event.user.id == user.id

        is PresenceUpdateEvent -> event.user.id == user.id.value

        is VoiceStateUpdateEvent -> event.state.userId == user.id

        is MessageCreateEvent -> event.message.author?.id == user.id
        is MessageUpdateEvent -> event.new.author?.id == user.id.value
        is MessageDeleteEvent -> event.message?.author?.id == user.id

        else -> true
    }

}