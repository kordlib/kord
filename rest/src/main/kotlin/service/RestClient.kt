package dev.kord.rest.service

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import dev.kord.rest.request.KtorRequestHandler
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public class RestClient(requestHandler: RequestHandler) : RestService(requestHandler) {
    public val auditLog: AuditLogService = AuditLogService(requestHandler)
    public val channel: ChannelService = ChannelService(requestHandler)
    public val emoji: EmojiService = EmojiService(requestHandler)
    public val guild: GuildService = GuildService(requestHandler)
    public val invite: InviteService = InviteService(requestHandler)
    public val user: UserService = UserService(requestHandler)
    public val voice: VoiceService = VoiceService(requestHandler)
    public val webhook: WebhookService = WebhookService(requestHandler)
    public val application: ApplicationService = ApplicationService(requestHandler)
    public val template: TemplateService = TemplateService(requestHandler)
    public val interaction: InteractionService = InteractionService(requestHandler)
    public val stageInstance: StageInstanceService = StageInstanceService(requestHandler)
    public val sticker: StickerService = StickerService(requestHandler)

    /**
     * Sends a request to the given [route]. This function exposes a direct call to the Discord api and allows
     * the user to send a custom [RequestBuilder.body].
     *
     * Unless such functionality is specifically needed, users are advised to use the safer [RestService] calls.
     *
     * @param route The route to which to send a request.
     * @param block The configuration for this request.
     */
    @KordUnsafe
    @KordExperimental
    public suspend inline fun <T> unsafe(route: Route<T>, block: RequestBuilder<T>.() -> Unit): T {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        return call(route) {
            block()
        }
    }
}

public fun RestClient(token: String): RestClient {
    val requestHandler = KtorRequestHandler(token)
    return RestClient(requestHandler)
}
