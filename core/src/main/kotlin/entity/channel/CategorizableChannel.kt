package dev.kord.core.entity.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.behavior.channel.CategorizableChannelBehavior
import dev.kord.core.behavior.channel.CategoryBehavior
import dev.kord.core.behavior.channel.createInvite
import dev.kord.core.entity.Invite
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.InviteCreateBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * An instance of a Discord channel associated to a [category].
 */
public interface CategorizableChannel : TopGuildChannel, CategorizableChannelBehavior {

    /**
     * The id of the [category] this channel belongs to, if any.
     */
    public val categoryId: Snowflake?
        get() = data.parentId.value

    /**
     * The category behavior this channel belongs to, if any.
     */
    public val category: CategoryBehavior?
        get() = when (val categoryId = categoryId) {
            null -> null
            else -> CategoryBehavior(id = categoryId, guildId = guildId, kord = kord)
        }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): CategorizableChannel
}

/**
 * Request to create an invite for this channel.
 *
 * @return the created [Invite].
 * @throws RestRequestException if something went wrong during the request.
 */
@Deprecated(
    "Use 'CategorizableChannelBehavior.createInvite' instead.",
    ReplaceWith("this.createInvite(builder)", "dev.kord.core.behavior.channel.createInvite"),
    DeprecationLevel.HIDDEN,
)
public suspend inline fun CategorizableChannel.createInvite(builder: InviteCreateBuilder.() -> Unit = {}): Invite {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    return (this as CategorizableChannelBehavior).createInvite(builder)
}
