package dev.kord.common

import dev.kord.common.annotation.KordExperimental
import dev.kord.common.annotation.KordUnsafe
import kotlinx.atomicfu.atomic

@KordExperimental
public object KordConfiguration {
    // not able to write it like `public var REST_VERSION: Int by atomic(10)` because of
    // https://github.com/Kotlin/kotlinx.atomicfu/issues/186
    // TODO use delegation when AtomicFU fixes this

    private const val REST_GATEWAY_DEFAULT = 10


    private val REST = atomic(REST_GATEWAY_DEFAULT)

    /**
     * The [version of Discord's REST API](https://discord.com/developers/docs/reference#api-versioning) Kord uses.
     *
     * Changing this version might lead to errors since Kord is designed to work with the initially set version.
     */
    @KordExperimental
    @set:KordUnsafe
    public var REST_VERSION: Int
        get() = REST.value
        set(value) {
            REST.value = value
        }


    private val GATEWAY = atomic(REST_GATEWAY_DEFAULT)

    /**
     * The [version of Discord's Gateway](https://discord.com/developers/docs/topics/gateway#gateways-gateway-versions)
     * Kord uses.
     *
     * Changing this version might lead to errors since Kord is designed to work with the initially set version.
     */
    @KordExperimental
    @set:KordUnsafe
    public var GATEWAY_VERSION: Int
        get() = GATEWAY.value
        set(value) {
            GATEWAY.value = value
        }


    private val VOICE = atomic(4)

    /**
     * The
     * [version of Discord's Voice Gateway](https://discord.com/developers/docs/topics/voice-connections#voice-gateway-versioning)
     * Kord uses.
     *
     * Changing this version might lead to errors since Kord is designed to work with the initially set version.
     */
    @KordExperimental
    @set:KordUnsafe
    public var VOICE_GATEWAY_VERSION: Int
        get() = VOICE.value
        set(value) {
            VOICE.value = value
        }
}
