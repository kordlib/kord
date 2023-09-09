package dev.kord.core.entity

import dev.kord.common.entity.Snowflake
import dev.kord.common.exception.RequestException
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.GuildWidgetData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.exception.EntityNotFoundException
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.guild.GuildWidgetModifyBuilder
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// TODO confirm this is actually a guild widget and not a guild widget settings object
public class GuildWidget(
    public val data: GuildWidgetData,
    public val guildId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : KordObject, Strategizable {

    /** Whether the widget is enabled or not. */
    public val isEnabled: Boolean get() = data.enabled

    /** The [GuildBehavior] for the widget. */
    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    /** The ID of the channel the widget is for. */
    public val channelId: Snowflake? get() = data.channelId

    /** The [ChannelBehavior] of the channel the widget is for. */
    public val channel: ChannelBehavior? get() = data.channelId?.let { ChannelBehavior(it, kord) }

    /**
     * Requests the [Guild] with the given [guildId].
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     * @throws EntityNotFoundException if the guild is null.
     */
    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    /**
     * Requests the [Guild] with the given [guildId], returns `null` when the guild isn't present.
     *
     * @throws RequestException if something went wrong while retrieving the guild.
     */
    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    /**
     * Requests the [Channel] with the given [channelId] as type [TopGuildChannel], returns null if the
     * channel isn't present or if the channel is not of type [TopGuildChannel].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend fun getChannelOrNull(): TopGuildChannel? = data.channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Requests the [Channel] with the given [channelId] as type [T], returns null if the
     * channel isn't present or if the channel is not of type [T].
     *
     * @throws RequestException if something went wrong while retrieving the channel.
     */
    public suspend inline fun <reified T : Channel> getChannelOfOrNull(): T? =
        data.channelId?.let { supplier.getChannelOfOrNull(it) }

    /**
     * Request to edit this [GuildWidget] and returns the edited widget.
     *
     * @throws RequestException if something went wrong during the request
     */
    public suspend inline fun edit(builder: GuildWidgetModifyBuilder.() -> Unit): GuildWidget {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return GuildWidget(GuildWidgetData.from(kord.rest.guild.modifyGuildWidget(guildId, builder)), guildId, kord)
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildWidget =
        GuildWidget(data, guildId, kord, strategy.supply(kord))

}
