package dev.kord.rest.service

import dev.kord.common.KordConstants
import dev.kord.common.annotation.KordExperimental
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.UserAgent
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public abstract class RestService(@PublishedApi internal val requestHandler: RequestHandler) {

    @PublishedApi
    internal suspend inline fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit = {}): T {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val request = RequestBuilder(route)
            .apply(builder)
            .apply {
                @OptIn(KordExperimental::class)
                unencodedHeader(UserAgent, KordConstants.USER_AGENT)
                if (route.requiresAuthorizationHeader) {
                    unencodedHeader(Authorization, "Bot ${requestHandler.token}")
                }
            }
            .build()
        return requestHandler.handle(request)
    }
}
