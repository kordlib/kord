package dev.kord.rest.service

import dev.kord.common.entity.ApplicationEmojis
import dev.kord.common.entity.DiscordApplication
import dev.kord.common.entity.DiscordEmoji
import dev.kord.common.entity.Snowflake
import dev.kord.rest.Image
import dev.kord.rest.builder.guild.EmojiCreateBuilder
import dev.kord.rest.builder.guild.EmojiModifyBuilder
import dev.kord.rest.json.request.EmojiCreateRequest
import dev.kord.rest.json.request.EmojiModifyRequest
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class ApplicationService(handler: RequestHandler) : RestService(handler) {

    public suspend fun getCurrentApplicationInfo(): DiscordApplication = call(Route.CurrentApplicationInfo)

    public suspend fun getApplicationEmojis(appId: Snowflake): ApplicationEmojis = call(Route.GetApplicationEmojis) {
        keys[Route.ApplicationId] = appId
    }

    public suspend fun getApplicationEmoji(appId: Snowflake, emojiId: Snowflake): DiscordEmoji =
        call(Route.GetApplicationEmoji) {
            keys[Route.ApplicationId] = appId
            keys[Route.EmojiId] = emojiId
        }

    public suspend fun createApplicationEmoji(appId: Snowflake, request: EmojiCreateRequest): DiscordEmoji =
        call(Route.PostApplicationEmoji) {
            keys[Route.ApplicationId] = appId

            body(EmojiCreateRequest.serializer(), request)
        }

    public suspend fun createApplicationEmoji(
        appId: Snowflake,
        name: String,
        image: Image,
    ): DiscordEmoji = createApplicationEmoji(appId, EmojiCreateBuilder(name, image).toRequest())

    public suspend inline fun modifyApplicationEmoji(
        appId: Snowflake,
        emojiId: Snowflake,
        request: EmojiModifyRequest
    ): DiscordEmoji =
        call(Route.PatchApplicationEmoji) {
            keys[Route.ApplicationId] = appId
            keys[Route.EmojiId] = emojiId

            body(EmojiModifyRequest.serializer(), request)
        }

    public suspend inline fun modifyApplicationEmoji(
        appId: Snowflake,
        emojiId: Snowflake,
        builder: EmojiModifyBuilder.() -> Unit
    ): DiscordEmoji {
        contract { callsInPlace(builder, InvocationKind.EXACTLY_ONCE) }
        return modifyApplicationEmoji(appId, emojiId, EmojiModifyBuilder().apply(builder).toRequest())
    }

    public suspend inline fun deleteApplicationEmoji(
        appId: Snowflake,
        emojiId: Snowflake,
    ): Unit = call(Route.DeleteApplicationEmoji) {
            keys[Route.ApplicationId] = appId
            keys[Route.EmojiId] = emojiId
        }
}
