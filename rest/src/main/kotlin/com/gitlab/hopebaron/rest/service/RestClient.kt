package com.gitlab.hopebaron.rest.service

import com.gitlab.hopebaron.common.annotation.KordExperimental
import com.gitlab.hopebaron.rest.ratelimit.RequestHandler
import com.gitlab.hopebaron.rest.request.RequestBuilder
import com.gitlab.hopebaron.rest.route.Route

class RestClient constructor(requestHandler: RequestHandler) : RestService(requestHandler) {
    val auditLog: AuditLogService = AuditLogService(requestHandler)
    val channel: ChannelService = ChannelService(requestHandler)
    val emoji: EmojiService = EmojiService(requestHandler)
    val guild: GuildService = GuildService(requestHandler)
    val invite: InviteService = InviteService(requestHandler)
    val user: UserService = UserService(requestHandler)
    val voice: VoiceService = VoiceService(requestHandler)

    /**
     * Sends a request to the given [route].
     *
     * @param route The route to which to send a request.
     * @param block The configuration for this request.
     */
    @KordExperimental
    suspend inline fun <T> unsafe(route: Route<T>, block: RequestBuilder<T>.() -> Unit): T = call(route) {
        block()
    }
}
