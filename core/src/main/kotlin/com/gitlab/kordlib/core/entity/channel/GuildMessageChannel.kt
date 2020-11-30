package dev.kord.core.entity.channel

import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord message channel associated to a [guild].
 */
interface GuildMessageChannel : CategorizableChannel, MessageChannel, GuildMessageChannelBehavior {

    /**
     * The channel topic, if present.
     */
    val topic: String? get() = data.topic.value

    /**
     * Returns a new [GuildMessageChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildMessageChannel

}