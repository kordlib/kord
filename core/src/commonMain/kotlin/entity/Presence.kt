package dev.kord.core.entity

import dev.kord.common.entity.PresenceStatus
import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.cache.data.ClientStatusData
import dev.kord.core.cache.data.PresenceData
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class Presence(
    public val data: PresenceData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : KordObject, Strategizable {

    public val activities: List<Activity> get() = data.activities.map { Activity(it) }

    public val clientStatus: ClientStatus get() = ClientStatus(data.clientStatus)

    public val guildId: Snowflake get() = data.guildId

    public val status: PresenceStatus get() = data.status

    public val userId: Snowflake get() = data.userId

    /**
     * Requests to get the user of this presence.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [User] wasn't present.
     */
    public suspend fun getUser(): User = supplier.getUser(userId)

    /**
     * Requests to get the user of this presence,
     * returns null if the [User] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getUserOrNull(): User? = supplier.getUserOrNull(userId)

    /**
     * Returns a new [Presence] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Presence =
        Presence(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "Presence(data=$data, kord=$kord, supplier=$supplier)"
    }

}

public class ClientStatus(public val data: ClientStatusData) {
    public val desktop: Client.Desktop? get() = data.desktop.value?.let { Client.Desktop(it) }
    public val mobile: Client.Mobile? get() = data.mobile.value?.let { Client.Mobile(it) }
    public val web: Client.Web? get() = data.web.value?.let { Client.Web(it) }

    override fun toString(): String {
        return "ClientStatus(data=$data)"
    }

    public sealed class Client(public val status: PresenceStatus) {
        public class Desktop(status: PresenceStatus) : Client(status)
        public class Mobile(status: PresenceStatus) : Client(status)
        public class Web(status: PresenceStatus) : Client(status)
    }
}
