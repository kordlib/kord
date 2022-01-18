package dev.kord.rest.service

import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import io.ktor.http.HttpHeaders.Authorization
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class RestService(@PublishedApi internal val requestHandler: RequestHandler) {

    @PublishedApi
    internal suspend inline fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit = {}): T {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val request = RequestBuilder(route)
            .apply(builder)
            .apply {
                if (route.requiresAuthorizationHeader) {
                    unencodedHeader(Authorization, "Bot ${requestHandler.token}")
                }
            }
            .build()
        return requestHandler.handle(request)
    }
}
