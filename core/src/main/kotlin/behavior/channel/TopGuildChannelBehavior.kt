package dev.kord.core.behavior.channel

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.entity.PermissionOverwrite
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.rest.builder.channel.ChannelPermissionModifyBuilder
import dev.kord.rest.request.RestRequestException
import dev.kord.rest.service.editMemberPermissions
import dev.kord.rest.service.editRolePermission
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.withIndex
import java.util.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a non-thread Discord channel associated to a [guild].
 *
 * 'Top' channels are those that do not require a parent channel to be created, and can be found at the top of the UI's hierarchy.
 */
public interface TopGuildChannelBehavior : GuildChannelBehavior {

    /**
     * Requests to get this behavior as a [TopGuildChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild channel.
     */
    override suspend fun asChannel(): TopGuildChannel = super.asChannel() as TopGuildChannel

    /**
     * Requests to get this behavior as a [TopGuildChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): TopGuildChannel? = super.asChannelOrNull() as? TopGuildChannel

    /**
     * Retrieve the [TopGuildChannel] associated with this behaviour from the provided [EntitySupplier]
     *
     * @throws [RequestException] if anything went wrong during the request.
     * @throws [EntityNotFoundException] if the user wasn't present.
     */
    override suspend fun fetchChannel(): TopGuildChannel = super.fetchChannel() as TopGuildChannel


    /**
     * Retrieve the [TopGuildChannel] associated with this behaviour from the provided [EntitySupplier]
     * returns null if the [TopGuildChannel] isn't present.
     *
     * @throws [RequestException] if anything went wrong during the request.
     */
    override suspend fun fetchChannelOrNull(): TopGuildChannel? = super.fetchChannelOrNull() as? TopGuildChannel

    /**
     * Requests to add or replace a [PermissionOverwrite] to this entity.
     *
     * @param reason the reason showing up in the audit log
     * @throws [RestRequestException] if something went wrong during the request.
     */
    public suspend fun addOverwrite(overwrite: PermissionOverwrite, reason: String? = null) {
        kord.rest.channel.editChannelPermissions(
            channelId = id,
            overwriteId = overwrite.target,
            permissions = overwrite.asRequest(),
            reason = reason
        )
    }

    /**
     * Requests to get the position of this channel in the [guild], as displayed in Discord.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    public suspend fun getPosition(): Int = supplier.getGuildChannels(guildId).withIndex().first { it.value.id == id }.index


    /**
     * Returns a new [TopGuildChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(
        strategy: EntitySupplyStrategy<*>
    ): TopGuildChannelBehavior = TopGuildChannelBehavior(guildId, id, kord, strategy)


}

internal fun TopGuildChannelBehavior(
    guildId: Snowflake,
    id: Snowflake,
    kord: Kord,
    strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
): TopGuildChannelBehavior = object : TopGuildChannelBehavior {
    override val guildId: Snowflake = guildId
    override val id: Snowflake = id
    override val kord: Kord = kord
    override val supplier: EntitySupplier = strategy.supply(kord)

    override fun hashCode(): Int = Objects.hash(id, guildId)

    override fun equals(other: Any?): Boolean = when (other) {
        is GuildChannelBehavior -> other.id == id && other.guildId == guildId
        is ChannelBehavior -> other.id == id
        else -> false
    }

    override fun toString(): String {
        return "TopGuildChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
    }
}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [roleId].
 *
 *  @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun TopGuildChannelBehavior.editRolePermission(
    roleId: Snowflake,
    builder: ChannelPermissionModifyBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.channel.editRolePermission(channelId = id, roleId = roleId, builder = builder)
}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [memberId].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
public suspend inline fun TopGuildChannelBehavior.editMemberPermission(
    memberId: Snowflake,
    builder: ChannelPermissionModifyBuilder.() -> Unit
) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.channel.editMemberPermissions(channelId = id, memberId = memberId, builder = builder)
}
