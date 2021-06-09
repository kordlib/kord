package dev.kord.core.event.guild

import dev.kord.common.annotation.DeprecatedSinceKord
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.*
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

private const val deprecationMessage = "The full member is now available in this Event."

class MemberUpdateEvent(
    val member: Member,
    val old: Member?,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    val guildId: Snowflake get() = member.guildId

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(deprecationMessage, ReplaceWith("member.id"), level = DeprecationLevel.ERROR)
    val memberId: Snowflake by member::id

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(deprecationMessage, ReplaceWith("member"), level = DeprecationLevel.ERROR)
    val user: User by ::member

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(deprecationMessage, ReplaceWith("member.roleIds"), level = DeprecationLevel.ERROR)
    val currentRoleIds: Set<Snowflake> by member::roleIds

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(deprecationMessage, ReplaceWith("member.nickname"), level = DeprecationLevel.ERROR)
    val currentNickName: String? by member::nickname

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(deprecationMessage, ReplaceWith("member.premiumSince"), level = DeprecationLevel.ERROR)
    val premiumSince: Instant? by member::premiumSince

    val guild: GuildBehavior get() = member.guild

    @DeprecatedSinceKord("0.7.0")
    @Deprecated(deprecationMessage, ReplaceWith("member.roles"), level = DeprecationLevel.ERROR)
    val currentRoles: Flow<Role> by member::roles

    @DeprecatedSinceKord("0.7.0")
    @Suppress("RedundantSuspendModifier")
    @Deprecated(deprecationMessage, ReplaceWith("member"), level = DeprecationLevel.ERROR)
    suspend fun getMember(): Member = member

    @DeprecatedSinceKord("0.7.0")
    @Suppress("RedundantSuspendModifier")
    @Deprecated(deprecationMessage, ReplaceWith("member"), level = DeprecationLevel.ERROR)
    suspend fun getMemberOrNull(): Member? = member

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): MemberUpdateEvent =
        MemberUpdateEvent(member, old, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "MemberUpdateEvent(member=$member, old=$old, kord=$kord, shard=$shard, supplier=$supplier)"
    }

}