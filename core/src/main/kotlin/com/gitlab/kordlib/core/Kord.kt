package com.gitlab.kordlib.core

import com.gitlab.kordlib.cache.api.DataCache
import com.gitlab.kordlib.gateway.Gateway
import com.gitlab.kordlib.rest.service.RestClient

class Kord {

    val rest: RestClient = TODO()
    val gateway: Gateway = TODO()
    val cache: DataCache = TODO()
    val unsafe: Unsafe = Unsafe(this)
}
