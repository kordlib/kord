package dev.kord.rest.builder.auditlog

import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest
import dev.kord.rest.service.AuditLogService

public class AuditLogGetRequestBuilder : RequestBuilder<AuditLogGetRequest> {

    /**
     * The id of the user whose actions should be filtered for. `null` by default.
     */
    public var userId: Snowflake? = null

    /**
     * The type of [AuditLogEvent] which should be filtered for. `null` by default.
     */
    public var action: AuditLogEvent? = null

    /**
     * The time, represented as a Snowflake, after which entries are no longer returned.
     */
    public var before: Snowflake? = null

    /**
     * How many entries are returned.
     *
     * When used in a [direct rest request][AuditLogService.getAuditLogs]: default 50, minimum 1, maximum 100.
     *
     * When used through pagination in core module: `null` means no limit, must be positive otherwise.
     */
    public var limit: Int? = null

    override fun toRequest(): AuditLogGetRequest = AuditLogGetRequest(userId, action, before, limit)
}
