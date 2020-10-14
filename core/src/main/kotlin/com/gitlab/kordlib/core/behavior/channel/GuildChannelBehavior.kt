package com.gitlab.kordlib.core.behavior.channel

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.InviteData
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.GuildChannel
import com.gitlab.kordlib.core.exception.EntityNotFoundException
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy
import com.gitlab.kordlib.rest.builder.channel.ChannelPermissionModifyBuilder
import com.gitlab.kordlib.rest.request.RestRequestException
import com.gitlab.kordlib.rest.service.RestClient
import com.gitlab.kordlib.rest.service.editMemberPermissions
import com.gitlab.kordlib.rest.service.editRolePermission
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.withIndex
import java.util.*
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a Discord channel associated to a [guild].
 */
interface GuildChannelBehavior : ChannelBehavior, Strategizable {

    /**
     * The id of the guild this channel is associated to.
     */
    val guildId: Snowflake

    /**
     * The guild behavior this channel is associated to.
     */
    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * Requests to get the invites of this channel.
     *
     * This property is not resolvable through cache and will always use the [RestClient] instead.
     *
     * The returned flow is lazily executed, any [RequestException] will be thrown on
     * [terminal operators](https://kotlinlang.org/docs/reference/coroutines/flow.html#terminal-flow-operators) instead.
     */
    val invites: Flow<Invite>
        get() = flow {
            val responses = kord.rest.channel.getChannelInvites(id.value)

            for (response in responses) {
                val data = InviteData.from(response)

                emit(Invite(data, kord))
            }
        }

    /**
     * Requests to get this behavior as a [GuildChannel].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the channel wasn't present.
     * @throws [ClassCastException] if the channel isn't a guild channel.
     */
    override suspend fun asChannel(): GuildChannel = super.asChannel() as GuildChannel

    /**
     * Requests to get this behavior as a [GuildChannel],
     * returns null if the channel isn't present or if the channel isn't a guild channel.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    override suspend fun asChannelOrNull(): GuildChannel? = super.asChannelOrNull() as? GuildChannel

    /**
     * Requests to get this channel's [Guild].
     *
     * @throws [RequestException] if something went wrong during the request.
     * @throws [EntityNotFoundException] if the guild wasn't present.
     */
    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get this channel's [Guild],
     * returns null if the guild isn't present.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests to add or replace a [PermissionOverwrite] to this entity.
     *
     * @throws [RestRequestException] if something went wrong during the request.
     */
    suspend fun addOverwrite(overwrite: PermissionOverwrite) {
        kord.rest.channel.editChannelPermissions(channelId = id.value, overwriteId = overwrite.target.value, permissions = overwrite.asRequest())
    }

    /**
     * Requests to get the position of this channel in the [guild], as displayed in Discord,
     *.
     *
     * @throws [RequestException] if something went wrong during the request.
     */
    suspend fun getPosition(): Int = supplier.getGuildChannels(guildId).withIndex().first { it.value.id == id }.index

    override fun compareTo(other: Entity): Int {
        if (other !is GuildChannelBehavior) return super.compareTo(other)
        val discordOrder = compareBy<GuildChannelBehavior> { it.guildId }
                .thenBy { (it as? GuildChannel)?.guildId }
                .thenBy { it.id }

        return discordOrder.compare(this, other)
    }

    /**
     * Returns a new [GuildChannelBehavior] with the given [strategy].
     */
    override fun withStrategy(
            strategy: EntitySupplyStrategy<*>
    ): GuildChannelBehavior = GuildChannelBehavior(guildId, id, kord, strategy)

    companion object {
        internal operator fun invoke(
                guildId: Snowflake,
                id: Snowflake,
                kord: Kord,
                strategy: EntitySupplyStrategy<*> = kord.resources.defaultStrategy
        ): GuildChannelBehavior = object : GuildChannelBehavior {
            override val guildId: Snowflake = guildId
            override val id: Snowflake = id
            override val kord: Kord = kord
            override val supplier: EntitySupplier = strategy.supply(kord)

            override fun hashCode(): Int = Objects.hash(id, guildId)

            override fun equals(other: Any?): Boolean = when(other) {
                is GuildChannelBehavior -> other.id == id && other.guildId == guildId
                is ChannelBehavior -> other.id == id
                else -> false
            }

            override fun toString(): String {
                return "GuildChannelBehavior(id=$id, guildId=$guildId, kord=$kord, supplier=$supplier)"
            }
        }
    }

}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [roleId].
 *
 *  @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildChannelBehavior.editRolePermission(roleId: Snowflake, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.channel.editRolePermission(channelId = id.value, roleId = roleId.value, builder = builder)
}

/**
 * Requests to add or replace a [PermissionOverwrite] for the [memberId].
 *
 * @throws [RestRequestException] if something went wrong during the request.
 */
@OptIn(ExperimentalContracts::class)
suspend inline fun GuildChannelBehavior.editMemberPermission(memberId: Snowflake, builder: ChannelPermissionModifyBuilder.() -> Unit) {
    contract {
        callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
    }
    kord.rest.channel.editMemberPermissions(channelId = id.value, memberId = memberId.value, builder = builder)
}
