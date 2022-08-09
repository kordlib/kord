package dev.kord.core.gateway.handler

import dev.kord.cache.api.DataCache
import dev.kord.cache.api.put
import dev.kord.cache.api.query
import dev.kord.core.Kord
import dev.kord.core.cache.data.UserData
import dev.kord.core.cache.idEq
import dev.kord.core.entity.User
import dev.kord.core.event.user.UserUpdateEvent
import dev.kord.gateway.Event
import dev.kord.gateway.UserUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import dev.kord.core.event.Event as CoreEvent

internal class UserEventHandler(
    cache: DataCache
) : BaseGatewayEventHandler(cache) {

    override suspend fun handle(event: Event, shard: Int, kord: Kord): CoreEvent? = when (event) {
        is UserUpdate -> handle(event, shard, kord)
        else -> null
    }

    private suspend fun handle(event: UserUpdate, shard: Int, kord: Kord): UserUpdateEvent {
        val data = UserData.from(event.user)

        val old = cache.query<UserData> { idEq(UserData::id, data.id) }
            .asFlow().map { User(it, kord) }.singleOrNull()

        cache.put(data)
        val new = User(data, kord)

        return UserUpdateEvent(old, new, shard)
    }

}
