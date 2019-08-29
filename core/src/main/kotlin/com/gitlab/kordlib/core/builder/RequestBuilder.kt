package com.gitlab.kordlib.core.builder

interface RequestBuilder<T> {
    fun toRequest() : T
}

interface AuditRequestBuilder<T> : RequestBuilder<T> {
    var reason: String?
}