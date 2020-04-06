package com.gitlab.kordlib.rest.request

import com.gitlab.kordlib.rest.json.response.DiscordRequestException

/**
 * Handles Discord API requests.
 */
interface RequestHandler {

    /**
     * Executes the [request], abiding by the active rate limits and returning the response [R].
     * Throws an [RequestException] when a non-rate limit error response is returned.
     */
    @Throws(DiscordRequestException::class)
    suspend fun <B : Any,R>  handle(request: Request<B,R>): R

}