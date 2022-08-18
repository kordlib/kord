package dev.kord.core.event.guild

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.event.Event
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class VoiceServerUpdateEvent(
    public val token: String,
    public val guildId: Snowflake,
    public val endpoint: String?,
    override val kord: Kord,
    override val shard: Int,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Event, Strategizable {

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): VoiceServerUpdateEvent =
        VoiceServerUpdateEvent(token, guildId, endpoint, kord, shard, strategy.supply(kord))

    override fun toString(): String {
        return "VoiceServerUpdateEvent(token='$token', guildId=$guildId, endpoint='$endpoint', kord=$kord, shard=$shard, supplier=$supplier)"
    }
}
