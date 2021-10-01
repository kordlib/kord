package dev.kord.core.event.guild

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.MembersChunkData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Presence
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlin.coroutines.CoroutineContext


@DeprecatedSinceKord("0.7.0")
@Deprecated("Renamed to MembersChunkEvent", ReplaceWith("MembersChunkEvent"), DeprecationLevel.ERROR)
public typealias MemberChunksEvent = MembersChunkEvent

public class MembersChunkEvent(
    public val data: MembersChunkData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
    override val coroutineContext: CoroutineContext = kord.coroutineContext,
) : Event, Strategizable {

    public val guildId: Snowflake get() = data.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val members: Set<Member>
        get() = data.members.zip(data.users)
            .map { (member, user) -> Member(member, user, kord) }
            .toSet()

    public val chunkIndex: Int get() = data.chunkIndex

    public val chunkCount: Int get() = data.chunkCount

    public val invalidIds: Set<Snowflake> get() = data.notFound.orEmpty()

    public val presences: List<Presence> get() = data.presences.orEmpty().map { Presence(it, kord) }

    public val nonce: String? get() = data.nonce.value

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MembersChunkEvent =
        MembersChunkEvent(data, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "MemberChunksEvent(guildId=$guildId, members=$members, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
