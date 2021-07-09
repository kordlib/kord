package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.UserBehavior
import dev.kord.core.cache.data.ThreadUserData
import dev.kord.core.supplier.EntitySupplier

data class ThreadUser(
    val data: ThreadUserData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : UserBehavior {
    override val id: Snowflake get() = data.userId
}
