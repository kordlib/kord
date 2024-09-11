package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.MembersChunkData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Presence
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

/**
 * The event dispatched in response to [Guild Request Members](https://discord.com/developers/docs/topics/gateway-events#request-guild-members).
 * Using [chunkIndex] and [chunkCount] you cna calculate how many chunks are left for your request.
 *
 * See [Guild Members Chunk](https://discord.com/developers/docs/topics/gateway-events#guild-members-chunk)
 */
public class MembersChunkEvent(
    public val data: MembersChunkData,
    override val kord: Kord,
    override val shard: Int,
    override val customContext: Any?,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {
    /**
     * The ID of the guild that triggered this event.
     */
    public val guildId: Snowflake get() = data.guildId

    /**
     * The [Guild] that triggered this event
     */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /**
     * A [Set] of guild [Member]s.
     */
    public val members: Set<Member>
        get() = data.members.zip(data.users)
            .map { (member, user) -> Member(member, user, kord) }
            .toSet()

    /**
     * The index of the expected chunks for the response. (0 <= [chunkIndex] < [chunkCount])
     */
    public val chunkIndex: Int get() = data.chunkIndex

    /**
     * The total number of expected chunks for this response
     */
    public val chunkCount: Int get() = data.chunkCount

    /**
     * A [Set] of IDs for users not found in this guild.
     */
    public val invalidIds: Set<Snowflake> get() = data.notFound.orEmpty()

    /**
     * A [List] of [Presence]s for the guild [Member]s.
     */
    public val presences: List<Presence> get() = data.presences.orEmpty().map { Presence(it, kord) }

    /**
     * The nonce used for the Guild Members request.
     */
    public val nonce: String? get() = data.nonce.value

    /**
     * Requests to get the [Guild] that triggered the event.
     *
     * @throws [RequestException] if anything went wrong during the request
     * @throws [EntityNotFoundException] if the guild was not present
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests to get the [Guild] that triggered the event, or `null` if the guild was not present
     *
     * @throws [RequestException] if anything went wrong during the request
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MembersChunkEvent =
        MembersChunkEvent(data, kord, shard, customContext, strategy.supply(kord))

    override fun toString(): String {
        return "MemberChunksEvent(guildId=$guildId, members=$members, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
