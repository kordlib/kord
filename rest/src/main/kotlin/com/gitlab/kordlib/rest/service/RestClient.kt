package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.common.annotation.KordExperimental
import com.gitlab.kordlib.common.annotation.KordUnsafe
import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route

class RestClient(requestHandler: RequestHandler) : RestService(requestHandler) {
    val auditLog: AuditLogService = AuditLogService(requestHandler)
    val channel: ChannelService = ChannelService(requestHandler)
    val emoji: EmojiService = EmojiService(requestHandler)
    val guild: GuildService = GuildService(requestHandler)
    val invite: InviteService = InviteService(requestHandler)
    val user: UserService = UserService(requestHandler)
    val voice: VoiceService = VoiceService(requestHandler)
    val webhook: WebhookService = WebhookService(requestHandler)
    val application: ApplicationService = ApplicationService(requestHandler)

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
    suspend inline fun <T> unsafe(route: Route<T>, block: RequestBuilder<T>.() -> Unit): T = call(route) {
        block()
    }
}
