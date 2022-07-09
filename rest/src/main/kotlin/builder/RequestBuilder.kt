package dev.kord.rest.builder

import dev.kord.common.annotation.KordDsl

@KordDsl
public interface AuditBuilder {
    /**
     * The reason for this request, this will be displayed in the audit log.
     */
    public var reason: String?
}

@KordDsl
public interface RequestBuilder<out T> {
    public fun toRequest(): T
}

@KordDsl
public interface AuditRequestBuilder<out T> : AuditBuilder, RequestBuilder<T>
