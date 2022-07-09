package dev.kord.rest.request

import dev.kord.common.KordConstants
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.UserAgent

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

    public suspend fun <T> intercept(builder: RequestBuilder<T>) {
        builder.apply {
            unencodedHeader(UserAgent, KordConstants.USER_AGENT)
            if (route.requiresAuthorizationHeader) {
                unencodedHeader(Authorization, "Bot $token")
            }
        }
    }
}
