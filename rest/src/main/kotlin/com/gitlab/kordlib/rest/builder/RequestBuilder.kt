package com.gitlab.kordlib.rest.builder

import com.gitlab.kordlib.common.annotation.KordDsl

@KordDsl
interface RequestBuilder<T> {
    fun toRequest(): T
}

@KordDsl
interface AuditRequestBuilder<T> : RequestBuilder<T> {
    /**
     * The reason for this request, this will be displayed in the audit log.
     */
    var reason: String?
}
