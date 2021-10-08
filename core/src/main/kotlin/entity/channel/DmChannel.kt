package dev.kord.core.entity.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.User
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import java.util.*

/**
 * An instance of a Discord DM channel.
 */
public data class DmChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : MessageChannel {
    /**
     * The ids of the recipients of the channel.
     */
    public val recipientIds: Set<Snowflake>
        get() = data.recipients.orEmpty().toSet()

    /**
     * The behaviors of the recipients of the channel.
     */
    public val recipientBehaviors: Set<UserBehavior> get() = recipientIds.map { UserBehavior(it, kord) }.toSet()

    /**
     * Requests to get the recipients of the channel.
     *
     * This request uses state [data] to resolve the entities belonging to the flow,
     * as such it can't guarantee an up to date representation if the [data] is outdated.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    public val recipients: Flow<User>
        get() = data.recipients.orEmpty().asFlow()
            .map { supplier.getUserOrNull(it) }
            .filterNotNull()

    override suspend fun asChannel(): MessageChannel = this

    override suspend fun asChannelOrNull(): MessageChannel = this

    /**
     * returns a new [DmChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): DmChannel =
        DmChannel(data, kord, strategy.supply(kord))


    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "DmChannel(data=$data, kord=$kord, supplier=$supplier)"
    }

}
