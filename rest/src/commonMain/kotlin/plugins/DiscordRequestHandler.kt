package dev.kord.rest.plugins

import dev.kord.rest.ratelimit.ExclusionRequestRateLimiter
import dev.kord.rest.ratelimit.RequestRateLimiter
import dev.kord.rest.ratelimit.RequestResponse
import dev.kord.rest.ratelimit.RequestToken
import dev.kord.rest.request.RequestIdentifier
import dev.kord.rest.request.from
import io.ktor.client.plugins.api.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.datetime.Clock

public class DiscordRatelimitPluginConfig {
    public var requestRateLimiter: RequestRateLimiter = ExclusionRequestRateLimiter()
    public var clock: Clock = Clock.System
}

public var DiscordRatelimitPlugin: ClientPlugin<DiscordRatelimitPluginConfig> =  createClientPlugin("DiscordRatelimitPlugin", ::DiscordRatelimitPluginConfig) {
    val requestRateLimiter = pluginConfig.requestRateLimiter
    val clock = pluginConfig.clock
    val requestTokenKey = AttributeKey<RequestToken>("requestToken")
    onRequest { request, _ ->
        val requestToken = requestRateLimiter.await(request)
        request.attributes.put(requestTokenKey,requestToken)
    }
    onResponse { response ->
        val requestToken = response.call.attributes[requestTokenKey]
        requestToken.complete(RequestResponse.from(response, clock))
    }
}
