package com.gitlab.kordlib.rest.service

import com.gitlab.kordlib.rest.request.RequestHandler
import com.gitlab.kordlib.rest.request.RequestBuilder
import com.gitlab.kordlib.rest.route.Route
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class RestService(@PublishedApi internal val requestHandler: RequestHandler) {

    @OptIn(ExperimentalContracts::class)
    @PublishedApi
    internal suspend inline fun <T> call(route: Route<T>, builder: RequestBuilder<T>.() -> Unit = {}): T {
        contract {
            callsInPlace(builder, InvocationKind.EXACTLY_ONCE)
        }
        val request = RequestBuilder(route).apply(builder).build()
        return requestHandler.handle(request)
    }

}

