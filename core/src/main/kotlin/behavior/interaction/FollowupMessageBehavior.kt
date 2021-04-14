package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.ChannelBehavior
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.entity.KordEntity

/**
 * The behavior of a [Discord Followup Message](https://discord.com/developers/docs/interactions/slash-commands#followup-messages)
 */
@KordPreview
interface FollowupMessageBehavior : KordEntity {

    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake

    val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)
}