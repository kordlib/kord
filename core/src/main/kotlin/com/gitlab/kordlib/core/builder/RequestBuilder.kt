package com.gitlab.kordlib.core.builder

@KordDsl
interface RequestBuilder<T> {
    fun toRequest() : T
}

@KordDsl
interface AuditRequestBuilder<T> : RequestBuilder<T> {
    var reason: String?
}
