package dev.kord.rest.builder.auditlog

import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest

public class AuditLogGetRequestBuilder : RequestBuilder<AuditLogGetRequest> {

    /**
     * The id of the user whose actions should be filtered for. `null` be default.
     */
    public var userId: Snowflake? = null

    /**
     * The type of [AuditLogEvent] which should be filtered for. `null` be default.
     */
    public var action: AuditLogEvent? = null

    /**
     * The time, represented as a Snowflake, after which entries are no longer returned.
     */
    public var before: Snowflake? = null

    override fun toRequest(): AuditLogGetRequest = AuditLogGetRequest(userId, action, before, 100)
}
