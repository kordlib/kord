package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.entity.channel.Channel
import dev.kord.core.supplier.EntitySupplier

class DeletedThreadChannel(
    override val data: ChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : Channel {

    val guildId: Snowflake
        get() = data.guildId.value!!

    val parentId: Snowflake get() = data.parentId!!.value!!

}