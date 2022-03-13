package dev.kord.rest.request

import dev.kord.common.KordConstants
import dev.kord.rest.route.Route
import io.ktor.http.*


/**
 * Handles Discord API requests.
 */
public interface RequestHandler {

    /**
     * The Discord bot authorization token used on requests.
     */
    public val token: String

    /**
     * Executes the [request], abiding by the active rate limits and returning the response [R].
     * @throws RestRequestException when a non-rate limit error response is returned.
     */
    @Throws(RestRequestException::class)
    public suspend fun <B : Any, R> handle(request: Request<B, R>): R

    public suspend fun <T> intercept(builder: RequestBuilder<T>): RequestBuilder<T> {
        return builder
    }
}

public fun  <T> RequestBuilder<T>.defaultInterception(route: Route<T>, token: String): RequestBuilder<T> {
        if (route.requiresAuthorizationHeader) {
            unencodedHeader(HttpHeaders.UserAgent, KordConstants.USER_AGENT)
            unencodedHeader(HttpHeaders.Authorization, "Bot $token")
        }
    return this
}
