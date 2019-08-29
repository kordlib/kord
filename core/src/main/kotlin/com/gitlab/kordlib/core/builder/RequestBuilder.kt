package com.gitlab.kordlib.core.builder

@KordBuilder
interface RequestBuilder<T> {
    fun toRequest() : T
}

@KordBuilder
interface AuditRequestBuilder<T> : RequestBuilder<T> {
    var reason: String?
}
