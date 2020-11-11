package com.gitlab.kordlib.core.entity

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.WebhookType
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.behavior.WebhookBehavior
import com.gitlab.kordlib.core.behavior.channel.GuildMessageChannelBehavior
import com.gitlab.kordlib.core.behavior.channel.MessageChannelBehavior
import com.gitlab.kordlib.core.cache.data.WebhookData
import com.gitlab.kordlib.core.entity.channel.GuildMessageChannel
import com.gitlab.kordlib.core.entity.channel.MessageChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.core.supplier.getChannelOf
import com.gitlab.kordlib.core.supplier.getChannelOfOrNull
import java.util.*

data class Webhook(
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
    suspend fun getGuild(): Guild? = guildId?.let { supplier.getGuild(it) }

    /**
     * Requests to get the guild this webhook belongs to,
     * returns null if the [Guild] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = guildId?.let { supplier.getGuildOrNull(it) }

    /**
     * Requests to get the channel this webhook operates in.
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the [GuildMessageChannel] wasn't present.
     */
    suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    /**
     * Requests to get the channel this webhook operates in,
     * returns null if the [GuildMessageChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)


    /**
     * Returns a new [Webhook] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): Webhook =
            Webhook(data, kord, strategy.supply(kord))

    override fun hashCode(): Int = Objects.hash(id)

    override fun equals(other: Any?): Boolean = when(other) {
        is WebhookBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "Webhook(data=$data, kord=$kord, supplier=$supplier)"
    }

}