package dev.kord.core.behavior.interaction

import dev.kord.common.entity.MessageFlag
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.channel.MessageChannelBehavior
import dev.kord.core.cache.data.toData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.Strategizable
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.core.entity.interaction.FollowupMessage
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.core.supplier.getChannelOf
import dev.kord.core.supplier.getChannelOfOrNull
import dev.kord.rest.builder.message.modify.FollowupMessageModifyBuilder
import dev.kord.rest.request.RestRequestException
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * The behavior of a [Followup Message](https://discord.com/developers/docs/interactions/receiving-and-responding#followup-messages)
 */
public interface FollowupMessageBehavior : KordEntity, Strategizable {

    public val applicationId: Snowflake
    public val token: String
    public val channelId: Snowflake

    public val channel: MessageChannelBehavior get() = MessageChannelBehavior(channelId, kord)

    public suspend fun getChannel(): MessageChannel = supplier.getChannelOf(channelId)

    public suspend fun getChannelOrNull(): MessageChannel? = supplier.getChannelOfOrNull(channelId)

}

/**
 * Requests to edit this followup message.
 *
 * @return The edited [FollowupMessage] of the interaction response, either [public][PublicFollowupMessage] or
 * [ephemeral][EphemeralFollowupMessage].
 *
 * @throws RestRequestException if something went wrong during the request.
 */
public suspend inline fun FollowupMessageBehavior.edit(
    builder: FollowupMessageModifyBuilder.() -> Unit,
): FollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, builder)

    val isEphemeral = response.flags.value?.contains(MessageFlag.Ephemeral) ?: false
    val message = Message(response.toData(), kord)

    return when {
        isEphemeral -> EphemeralFollowupMessage(message, applicationId, token, kord)
        else -> PublicFollowupMessage(message, applicationId, token, kord)
    }
}
