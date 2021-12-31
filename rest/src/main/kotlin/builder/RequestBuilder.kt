package dev.kord.rest.builder

import dev.kord.common.annotation.KordDsl

@KordDsl
public interface RequestBuilder<T> {
    public fun toRequest(): T
}

@KordDsl
public interface AuditRequestBuilder<T> : RequestBuilder<T> {
    /**
     * The reason for this request, this will be displayed in the audit log.
     */
    public var reason: String?
}
