package dev.kord.core.behavior.interaction

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.PublicFollowupMessage
import dev.kord.rest.builder.interaction.PublicFollowupMessageModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface PublicFollowupMessageBehavior : FollowupMessageBehavior {
    suspend fun delete() {
        kord.rest.interaction.deleteFollowupMessage(applicationId, token, id)
    }
}

fun PublicFollowupMessageBehavior(
    id: Snowflake,
    applicationId: Snowflake,
    token: String,
    channelId: Snowflake,
    kord: Kord
) = object : PublicFollowupMessageBehavior {
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
suspend inline fun PublicFollowupMessageBehavior.edit(builder: PublicFollowupMessageModifyBuilder.() -> Unit): PublicFollowupMessage {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = PublicFollowupMessageModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, request)
    val data = MessageData.from(response)
    return PublicFollowupMessage(Message(data, kord), applicationId, token, kord)
}