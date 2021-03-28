package dev.kord.core.behavior

import dev.kord.common.annotation.KordPreview
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.cache.data.MessageData
import dev.kord.core.entity.KordEntity
import dev.kord.core.entity.Message
import dev.kord.core.entity.interaction.InteractionFollowup
import dev.kord.rest.builder.interaction.FollowupMessageModifyBuilder
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@KordPreview
interface InteractionFollowupBehavior : KordEntity {
    val applicationId: Snowflake
    val token: String
    val channelId: Snowflake

    suspend fun delete() {
        kord.rest.interaction.deleteFollowupMessage(applicationId, token, id)
    }
}

@KordPreview
fun InteractionFollowupBehavior(
        id: Snowflake,
        applicationId: Snowflake,
        channelId: Snowflake,
        token: String,
        kord: Kord
) = object : InteractionFollowupBehavior {
    override val id: Snowflake
        get() = id

    override val applicationId: Snowflake
        get() = applicationId

    override val token: String
        get() = token
    override val channelId: Snowflake
        get() = channelId

    override val kord: Kord
        get() = kord

}


@KordPreview
@OptIn(ExperimentalContracts::class)
suspend inline fun InteractionFollowupBehavior.edit(builder: FollowupMessageModifyBuilder.() -> Unit): InteractionFollowup {
    contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
    val request = FollowupMessageModifyBuilder().apply(builder).toRequest()
    val response = kord.rest.interaction.modifyFollowupMessage(applicationId, token, id, request)
    val data = MessageData.from(response)
    return InteractionFollowup(Message(data, kord), token, applicationId, kord)
}