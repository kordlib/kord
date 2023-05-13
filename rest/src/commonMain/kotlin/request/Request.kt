package dev.kord.rest.request

import io.ktor.client.request.*
import io.ktor.http.*

public val HttpRequestBuilder.identifier: RequestIdentifier
    get() { //The major identifier is always the 'biggest' entity.
        val firstParameter = url.parameters.entries().first()
        return if(firstParameter.key in RequestIdentifier.majorParameters)
            RequestIdentifier.MajorParamIdentifier(url, firstParameter.value.first())
        else RequestIdentifier.RouteIdentifier(url)

    }

/**
 * A ['per-route'](https://discord.com/developers/docs/topics/rate-limits) identifier for rate limiting purposes.
 */
public sealed class RequestIdentifier {

    public companion object {
        public val majorParameters: Array<String> = arrayOf("guild_id", "channel_id", "webhook_id", "interaction_token")

    }

    /**
     * An identifier that does not contain any major parameters.
     */
    public data class RouteIdentifier(val route: URLBuilder) : RequestIdentifier()

    /**
     * An identifier with a major parameter.
     */
    public data class MajorParamIdentifier(val route: URLBuilder, val param: String) : RequestIdentifier()
}
