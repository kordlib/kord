package dev.kord.rest.request

/**
 * Handles Discord API requests.
 */
public interface RequestHandler {

    /**
     * Executes the [request], abiding by the active rate limits and returning the response [R].
     * @throws RestRequestException when a non-rate limit error response is returned.
     */
    @Throws(RestRequestException::class)
    public suspend fun <B : Any, R> handle(request: Request<B, R>): R

}
