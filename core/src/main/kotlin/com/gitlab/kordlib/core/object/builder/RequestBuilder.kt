package com.gitlab.kordlib.core.`object`.builder

interface RequestBuilder<T> {
    fun toRequest() : T
}

interface AuditRequestBuilder<T> : RequestBuilder<T> {
    var reason: String?
}