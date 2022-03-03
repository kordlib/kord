package dev.kord.rest.service

import dev.kord.common.KordConstants
import dev.kord.rest.request.RequestBuilder
import dev.kord.rest.request.RequestHandler
import dev.kord.rest.route.Route
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.UserAgent
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

public abstract class RestService(@PublishedApi internal val requestHandler: RequestHandler) {

    @PublishedApi
    internal suspend inline fun <T> call(
        route: Route<T>,
        builder: RequestBuilder<T>.() -> Unit = {},
        baseUrl: String = Route.baseUrl
    ): T {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val builder = RequestBuilder(baseUrl, route)
            .apply(builder)
        val interceptedBuilder = requestHandler.intercept(builder)
        return requestHandler.handle(interceptedBuilder.build())
    }
}
