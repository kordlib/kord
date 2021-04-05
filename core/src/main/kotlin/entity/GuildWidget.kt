package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.GuildWidgetData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.GuildChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.guild.GuildWidgetModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

class GuildWidget(
        val data: GuildWidgetData,
        val guildId: Snowflake,
        override val kord: Kord,
        override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

    val isEnabled: Boolean get() = data.enabled

    val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    val channelId: Snowflake? get() = data.channelId

    val channel: ChannelBehavior? get() = data.channelId?.let { ChannelBehavior(it, kord) }

    suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    suspend fun getChannelOrNull(): GuildChannel? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    suspend inline fun <reified T : Channel> getChannelOfOrNull(): T? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    @OptIn(ExperimentalContracts::class)
    suspend inline fun edit(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return GuildWidget(GuildWidgetData.from(kord.rest.guild.modifyGuildWidget(guildId, builder)), guildId, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildWidget =
            GuildWidget(data, guildId, kord, strategy.supply(kord))

}