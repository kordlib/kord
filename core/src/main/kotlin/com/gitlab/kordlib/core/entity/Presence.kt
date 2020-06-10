package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.Status
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

    val game: Activity? get() = data.game?.let { Activity(it) }

    val guildId: Snowflake? get() = data.guildId?.let { Snowflake(it) }

    val roleIds: Set<Snowflake>? get() = data.roles?.asSequence()!!.map { Snowflake(it) }.toSet()

    val roles: Flow<Role>
        get() = if (guildId == null) emptyFlow()
        else supplier.getGuildRoles(guildId!!)

    val status: Status get() = data.status

    val userId: Snowflake get() = Snowflake(data.userId)

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

}

class ClientStatus(val data: ClientStatusData) {
    val desktop: Client.Desktop? get() = data.desktop?.let { Client.Desktop(it) }
    val mobile: Client.Mobile? get() = data.desktop?.let { Client.Mobile(it) }
    val web: Client.Web? get() = data.desktop?.let { Client.Web(it) }

    sealed class Client(val status: Status) {
        class Desktop(status: Status) : Client(status)
        class Mobile(status: Status) : Client(status)
        class Web(status: Status) : Client(status)
    }
}
