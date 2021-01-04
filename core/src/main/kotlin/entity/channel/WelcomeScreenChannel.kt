package dev.kord.core.entity.channel
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.cache.data.WelcomeScreenChannelData
import dev.kord.core.supplier.EntitySupplier

/**
 * One of the channels shown in the welcome screen
 *
 * @property id the id of the channel.
 * @property description the description shown for the channel.
 * @property emojiId the emoji id if the emoji is custom.
 * @property emojiName the emoji name if custom, the unicode character if standard, or `null` if no emoji is set.
 */
class WelcomeScreenChannel(
    val data: WelcomeScreenChannelData,
    override val kord: Kord,
    override val supplier: EntitySupplier = kord.defaultSupplier,
) : ChannelBehavior {

    override val id: Snowflake
        get() = data.channelId

    val description: String get() = data.description

    val emojiId: Snowflake? get() = data.emojiId

    val emojiName: String? get() = data.emojiName

}