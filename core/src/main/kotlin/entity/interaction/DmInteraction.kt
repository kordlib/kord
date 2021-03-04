package dev.kord.core.entity.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.InteractionData
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.DmChannel
import dev.kord.core.supplier.EntitySupplier

/**
 * An [Interaction] that took place in a [DmChannel].
 */
@KordPreview
class DmInteraction(
    override val data: InteractionData,
    override val applicationId: Snowflake,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : Interaction {
    /**
     * The user who invoked the interaction.
     */
    val user get() =  User(data.user.value!!, kord)
}