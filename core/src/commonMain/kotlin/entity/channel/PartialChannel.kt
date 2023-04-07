package dev.kord.core.entity.channel

import dev.kord.common.entity.ChannelType
import dev.kord.common.entity.Permissions
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.PartialChannelData
import dev.kord.core.cache.data.ThreadMetadataData
import dev.kord.core.entity.channel.thread.NewsChannelThread
import dev.kord.core.entity.channel.thread.TextChannelThread
import dev.kord.core.entity.channel.thread.ThreadChannel
import dev.kord.core.hash
import dev.kord.core.supplier.EntitySupplier
import dev.kord.core.supplier.EntitySupplyStrategy

public class PartialChannel(
    public val data: PartialChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier
) : ChannelBehavior {
    public val name: String? get() = data.name.value

    override val id: Snowflake get() = data.id

    public val type: ChannelType get() = data.type

    public val permissions: Permissions? get() = data.permissions.value

    public val parentId: Snowflake? get() = data.parentId?.value

    public val threadData: ThreadMetadataData? = data.threadMetadata.value

    override fun hashCode(): Int = hash(id)

    override fun equals(other: Any?): Boolean = when (other) {
        is ChannelBehavior -> other.id == id
        is TextChannel -> other.id == id
        is DmChannel -> other.id == id
        is StageChannel -> other.id == id
        is VoiceChannel -> other.id == id
        is Category -> other.id == id
        is NewsChannel -> other.id == id
        is ForumChannel -> other.id == id
        is NewsChannelThread -> other.id == id
        is TextChannelThread -> other.id == id
        is Channel -> other.id == id
        is ThreadChannel -> other.id == id
        else -> false
    }

    override fun withStrategy(strategy: EntitySupplyStrategy<*>): PartialChannel =
        PartialChannel(data, kord, strategy.supply(kord))

    override fun toString(): String {
        return "PartialChannel(data=$data, kord=$kord, supplier=$supplier)"
    }
}