package dev.kord.core.entity.onboarding

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.cache.data.GuildOnboardingData
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.TopGuildChannel
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter

public class GuildOnboarding(
    public val data: GuildOnboardingData,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : KordObject, Strategizable {
    public val guildId: Snowflake get() = data.guildId

    public val guild: GuildBehavior get() = GuildBehavior(guildId, kord)

    public val prompts: List<OnboardingPrompt> get() = data.prompts.map { OnboardingPrompt(it) }

    public val defaultChannelIds: List<Snowflake> get() = data.defaultChannelIds

    public val enabled: Boolean get() = data.enabled

    public suspend fun getGuild(): Guild = supplier.getGuild(guildId)

    public suspend fun getGuildOrNull(): Guild? = supplier.getGuildOrNull(guildId)

    public suspend fun getDefaultChannels(): Flow<TopGuildChannel> =
        supplier.getGuildChannels(guildId).filter { it.id in defaultChannelIds }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): GuildOnboarding =
        GuildOnboarding(data, kord, strategy.supply(kord))


}