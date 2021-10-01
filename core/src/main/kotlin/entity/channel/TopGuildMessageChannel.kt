package dev.kord.core.entity.channel

import dev.kord.core.behavior.channel.TopGuildMessageChannelBehavior
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * An instance of a Discord message channel associated to a [guild].
 */
public interface TopGuildMessageChannel : CategorizableChannel, GuildMessageChannel, TopGuildMessageChannelBehavior {

    /**
     * The channel topic, if present.
     */
    public val topic: String? get() = data.topic.value

    /**
     * Returns a new [TopGuildMessageChannel] with the given [strategy].
     */
    override fun withStrategy(strategy: EntitySupplyStrategy<*>): TopGuildMessageChannel

}
