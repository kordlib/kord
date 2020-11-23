package com.gitlab.kordlib.core.event.guild

import com.gitlab.kordlib.common.annotation.DeprecatedSinceKord
import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.common.entity.optional.Optional
import com.gitlab.kordlib.common.entity.optional.orEmpty
import com.gitlab.kordlib.core.Kord
import com.gitlab.kordlib.core.behavior.GuildBehavior
import com.gitlab.kordlib.core.cache.data.MemberData
import com.gitlab.kordlib.core.cache.data.MembersChunkData
import com.gitlab.kordlib.core.cache.data.PresenceData
import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.Member
import com.gitlab.kordlib.core.entity.Presence
import com.gitlab.kordlib.core.entity.Strategizable
import com.gitlab.kordlib.core.event.Event
import com.gitlab.kordlib.core.supplier.EntitySupplier
import com.gitlab.kordlib.core.supplier.EntitySupplyStrategy



@DeprecatedSinceKord("0.7.0")
@Deprecated("Renamed to MembersChunkEvent", ReplaceWith("MembersChunkEvent"), DeprecationLevel.ERROR)
typealias MemberChunksEvent = MembersChunkEvent

class MembersChunkEvent(
        val data: MembersChunkData,
        override val kord: Kord,
        override val shard: Int,
        override val supplier: EntitySupplier = kord.defaultSupplier
) : Event, Strategizable {

    val guildId: Snowflake get() = data.guildId

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val members: Set<Member> get() = data.members.zip(data.users)
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
