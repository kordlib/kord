package dev.kord.common

import dev.kord.common.annotation.KordUnsafe
import kotlin.concurrent.Volatile

public object KordConfiguration {

    private const val REST_GATEWAY_DEFAULT = 10

    /**
     * The [version of Discord's REST API](https://discord.com/developers/docs/reference#api-versioning) Kord uses.
     *
     * Changing this version might lead to errors since Kord is designed to work with the initially set version.
     */
    @Volatile
    @set:KordUnsafe
    public var REST_VERSION: Int = REST_GATEWAY_DEFAULT

    /**
     * The
     * [version of Discord's Gateway](https://discord.com/developers/docs/topics/gateway#connecting-gateway-url-query-string-params)
     * Kord uses.
     *
     * Changing this version might lead to errors since Kord is designed to work with the initially set version.
     */
    @Volatile
    @set:KordUnsafe
    public var GATEWAY_VERSION: Int = REST_GATEWAY_DEFAULT

    /**
     * The
     * [version of Discord's Voice Gateway](https://discord.com/developers/docs/topics/voice-connections#voice-gateway-versioning)
     * Kord uses.
     *
     * Changing this version might lead to errors since Kord is designed to work with the initially set version.
     */
    @Volatile
    @set:KordUnsafe
    public var VOICE_GATEWAY_VERSION: Int = 4
}
