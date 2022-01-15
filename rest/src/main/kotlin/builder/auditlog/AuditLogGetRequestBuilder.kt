package dev.kord.rest.builder.auditlog

import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest

class AuditLogGetRequestBuilder : RequestBuilder<AuditLogGetRequest> {

    /**
     * The id of the user whose actions should be filtered for. `null` by default.
     */
    var userId: Snowflake? = null

    /**
     * The type of [AuditLogEvent] which should be filtered for. `null` by default.
     */
    var action: AuditLogEvent? = null

    /**
     * The time, represented as a Snowflake, after which entries are no longer returned.
     */
    var before: Snowflake? = null

    /**
     * How many entries are returned (default 50, minimum 1, maximum 100).
     */
    var limit: Int = 50

    override fun toRequest(): AuditLogGetRequest = AuditLogGetRequest(userId, action, before, limit)
}
