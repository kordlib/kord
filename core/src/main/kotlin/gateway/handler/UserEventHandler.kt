package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.User
import dev.kord.core.event.user.UserUpdateEvent
import dev.kord.core.gateway.MasterGateway
import dev.kord.gateway.Event
import dev.kord.gateway.UserUpdate
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.kord.core.event.Event as CoreEvent

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

        val old = cache.query<UserData> { idEq(UserData::id, data.id) }
                .asFlow().map { User(it, kord) }.singleOrNull()

        cache.put(data)
        val new = User(data, kord)

        coreFlow.emit(UserUpdateEvent(old, new, shard))
    }

}