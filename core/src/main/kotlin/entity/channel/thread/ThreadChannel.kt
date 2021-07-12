package dev.kord.core.entity.channel.thread

import dev.kord.common.entity.Snowflake
import dev.kord.common.entity.optional.value
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.threads.ThreadChannelBehavior
import dev.kord.core.cache.data.ChannelData
import dev.kord.core.supplier.EntitySupplier

class ThreadChannel(val data: ChannelData, override val kord: Kord, override val supplier: EntitySupplier = kord.defaultSupplier) : ThreadChannelBehavior {

    private val threadData = data.threadsMetadata.value!!

    override val id: Snowflake get() = data.id

    override val guildId: Snowflake get() = data.guildId.value!!

    override val name: String get() = data.name.value!!

    val archived: Boolean get() = threadData.archived

    val locked: Boolean get() = threadData.locked.orElse(false)

    val archiverId: Snowflake? get() = threadData.archiverId.value

    val archiveTimeStamps: String get() = threadData.archiveTimestamp

    val autoArchiveDuration: Int get() = threadData.autoArchiveDuration

    val ratelimitPerUser: Int? get() = data.rateLimitPerUser.value

}