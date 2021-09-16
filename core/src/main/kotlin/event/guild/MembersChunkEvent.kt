package dev.kord.core.event.guild

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.orEmpty
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.MemberData
import dev.kord.core.cache.data.MembersChunkData
import dev.kord.core.cache.data.PresenceData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.Presence
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy


@DeprecatedSinceKord("0.7.0")
@Deprecated("Renamed to MembersChunkEvent", ReplaceWith("MembersChunkEvent"), DeprecationLevel.ERROR)
typealias MemberChunksEvent = MembersChunkEvent

class MembersChunkEvent(
    val data: MembersChunkData,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    override val guildId: Snowflake get() = data.guildId

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val members: Set<Member>
        get() = data.members.zip(data.users)
            .map { (member, user) -> Member(member, user, kord) }
            .toSet()

    val chunkIndex: Int get() = data.chunkIndex

    val chunkCount: Int get() = data.chunkCount

    val invalidIds: Set<Snowflake> get() = data.notFound.orEmpty()

    val presences: List<Presence> get() = data.presences.orEmpty().map { Presence(it, kord) }

    val nonce: String? get() = data.nonce.value

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MembersChunkEvent =
        MembersChunkEvent(data, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "MemberChunksEvent(guildId=$guildId, members=$members, kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
