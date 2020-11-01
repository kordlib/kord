package com.gitlab.kordlib.core.gateway.handler

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.cache.api.put
import com.gitlab.kordlib.cache.api.query
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.cache.data.UserData
import com.gitlab.kordlib.core.entity.User
import com.gitlab.kordlib.core.event.UserUpdateEvent
import com.gitlab.kordlib.core.gateway.MasterGateway
import com.gitlab.kordlib.gateway.Event
import com.gitlab.kordlib.gateway.UserUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import com.gitlab.kordlib.core.event.Event as CoreEvent

@Suppress("EXPERIMENTAL_API_USAGE")
internal class UserEventHandler(
        kord: Kord,
        gateway: MasterGateway,
        cache: DataCache,
        coreFlow: MutableSharedFlow<CoreEvent>
) : BaseGatewayEventHandler(kord, gateway, cache, coreFlow) {

    override suspend fun handle(event: Event, shard: Int) = when (event) {
        is UserUpdate -> handle(event, shard)
        else -> Unit
    }

    private suspend fun handle(event: UserUpdate, shard: Int) {
        val data = UserData.from(event.user)

        val old = cache.query<UserData> { UserData::id eq data.id }
                .asFlow().map { User(it, kord) }.singleOrNull()

        cache.put(data)
        val new = User(data, kord)

        coreFlow.emit(UserUpdateEvent(old, new, shard))
    }

}