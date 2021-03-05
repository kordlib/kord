package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.GuildBehavior
import dev.kord.core.behavior.GuildInteractionBehavior
import dev.kord.core.behavior.MemberBehavior
import dev.kord.core.behavior.channel.GuildMessageChannelBehavior
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.Guild
import dev.kord.core.supplier.EntitySupplier

/**
 * An [Interaction] that took place in a [Guild].
 */
@KordPreview
class GuildInteraction(
    override val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier
) : Interaction, GuildInteractionBehavior {

    override val guildId: Snowflake
        get() = data.guildId.value!!

    /**
     * Overridden permissions of the interaction invoker in the channel.
     */
    val permissions: Permissions get() = data.permissions.value!!

    /**
     * The [GuildBehavior] for the guild the command was executed in.
     */
    val guild get() = GuildBehavior(guildId, kord)

    /**
     * The invoker of the command as [MemberBehavior].
     */
    val member: MemberBehavior get() = MemberBehavior(guildId, data.member.value!!.userId, kord)

    override val channel: GuildMessageChannelBehavior
        get() = GuildMessageChannelBehavior(guildId, channelId, kord)


}