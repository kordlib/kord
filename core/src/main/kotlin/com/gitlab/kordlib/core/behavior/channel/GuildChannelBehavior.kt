package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.`object`.PermissionOverwrite
import com.gitlab.kordlib.core.`object`.builder.channel.NewInviteBuilder
import com.gitlab.kordlib.core.`object`.builder.channel.UpdateGuildChannelBuilder
import com.gitlab.kordlib.core.entity.Snowflake
import com.gitlab.kordlib.rest.json.request.EditChannelPermissionRequest
import kotlinx.coroutines.flow.Flow

interface GuildChannelBehavior<T : UpdateGuildChannelBuilder> : ChannelBehavior {

    suspend fun createInvite(builder: NewInviteBuilder): Nothing /*Invite*/ = TODO()

    suspend fun getInvites(): Flow<Nothing /*Invite*/> = TODO()

    suspend fun edit(builder: T): Nothing /*GuildChannel*/ = TODO()

    suspend fun editPermissions(overwrite: PermissionOverwrite) {
        val request = EditChannelPermissionRequest(overwrite.allowed, overwrite.denied, overwrite.type)
        kord.rest.channel.editChannelPermissions(id.value, overwrite.target.value, request)
    }

    suspend fun type() {
        kord.rest.channel.triggerTypingIndicator(id.value)
    }

    //TODO 1.3.50 add fun typeUntil(mark: ClockMark): Unit

    companion object {
        internal operator fun<T: UpdateGuildChannelBuilder> invoke(id: Snowflake, kord: Kord) = object : GuildChannelBehavior<T> {
            override val id: Snowflake = id
            override val kord: Kord = kord
        }
    }

}

