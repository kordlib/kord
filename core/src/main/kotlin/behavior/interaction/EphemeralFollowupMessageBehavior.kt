package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.EphemeralFollowupMessage
import dev.kord.rest.builder.interaction.EphemeralFollowupMessageModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface EphemeralFollowupMessageBehavior : FollowupMessageBehavior

fun EphemeralFollowupMessageBehavior(
    id: Snowflake,
    applicationId: Snowflake,
    token: String,
    channelId: Snowflake,
    kord: Kord
) = object : EphemeralFollowupMessageBehavior {
    override val applicationId: Snowflake
        get() = applicationId
    override val token: String
        get() = token
    override val channelId: Snowflake
        get() = channelId
    override val kord: Kord
        get() = kord
    override val id: Snowflake
        get() = id

}

@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun EphemeralFollowupMessageBehavior.edit(builder: EphemeralFollowupMessageModifyBuilder.() -> Unit): EphemeralFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = EphemeralFollowupMessageModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, request)
    val data = MessageData.from(response)
    return EphemeralFollowupMessage(Message(data, kord), applicationId, token, kord)
}