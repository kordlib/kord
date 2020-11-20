package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.PresenceStatus
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.KordObject
import com.gitlab.kordlib.core.cache.data.ClientStatusData
import com.gitlab.kordlib.core.cache.data.PresenceData
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class Presence(
        val data: PresenceData,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    val activities: List<Activity> get() = data.activities.map { Activity(it) }

    val clientStatus: ClientStatus get() = ClientStatus(data.clientStatus)

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Game field is no longer present.", ReplaceWith("activities.firstOrNull()"), DeprecationLevel.ERROR)
    val game: Activity? get() = activities.firstOrNull()

    val guildId: Snowflake? get() = data.guildId

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("role ids are no longer present.",  ReplaceWith("emptySet()") , DeprecationLevel.ERROR)
    val roleIds: Set<Snowflake>? get() = emptySet()

    @DeprecatedSinceKord("0.7.0")
    @Deprecated("role ids are no longer present.",  ReplaceWith("emptyFlow()") , DeprecationLevel.ERROR)
    val roles: Flow<Role> get() = emptyFlow()

    val status: PresenceStatus get() = data.status

    val userId: Snowflake get() = data.userId

    /**
     * Requests to get the user of this presence.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the user of this presence,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    /**
     * Returns a new [Presence] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Presence =
            Presence(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "Presence(data=$data, kord=$kord, supplier=$supplier)"
    }

}

class ClientStatus(val data: ClientStatusData) {
    val desktop: Client.Desktop? get() = data.desktop.value?.let { Client.Desktop(it) }
    val mobile: Client.Mobile? get() = data.mobile.value?.let { Client.Mobile(it) }
    val web: Client.Web? get() = data.web.value?.let { Client.Web(it) }

    override fun toString(): String {
        return "ClientStatus(data=$data)"
    }

    sealed class Client(val status: PresenceStatus) {
        class Desktop(status: PresenceStatus) : Client(status)
        class Mobile(status: PresenceStatus) : Client(status)
        class Web(status: PresenceStatus) : Client(status)
    }
}
