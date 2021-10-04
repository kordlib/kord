package dev.kord.core.entity

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.WebhookType
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.WebhookBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.WebhookData
import dev.kord.core.entity.channel.TopGuildMessageChannel
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import java.util.*

public data class Webhook(
    val data: WebhookData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : WebhookBehavior, Strategizable {

    override val id: Snowflake get() = data.id

    val type: WebhookType get() = data.type

    val creatorId: Snowflake? get() = data.userId.value

    val channelId: Snowflake get() = data.channelId

    val guildId: Snowflake? get() = data.guildId.value

    val name: String? get() = data.name

    val token: String? get() = data.token.value

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    val guild: GuildBehavior? get() = guildId?.let { GuildBehavior(it, kord) }

    /**
     * Requests to get the guild this webhook belongs to.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [Guild] wasn't present.
     */
    @DeprecatedSinceKord("0.7.0")
    @Deprecated("Use getGuildOrNull instead.", ReplaceWith("getGuildOrNull()"), level = DeprecationLevel.ERROR)
    public suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuild(it) }

    /**
     * Requests to get the guild this webhook belongs to,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Requests to get the channel this webhook operates in.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [TopGuildMessageChannel] wasn't present.
     */
    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel this webhook operates in,
     * returns null if the [TopGuildMessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)


    /**
     * Returns a new [Webhook] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Webhook =
        Webhook(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is WebhookBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Webhook(data=$data, kord=$kord, supplier=$supplier)"
    }

}
