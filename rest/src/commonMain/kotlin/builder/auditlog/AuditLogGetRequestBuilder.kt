package dev.kord.rest.builder.auditlog

import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.json.request.AuditLogGetRequest
import dev.kord.rest.service.AuditLogService

@KordDsl
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
     * The time, represented as a Snowflake, before which entries are no longer returned.
     */
    public var after: Snowflake? = null

    /**
     * How many entries are returned.
     *
     * When used in a [direct rest request][AuditLogService.getAuditLogs]: default 50, minimum 1, maximum 100.
     *
     * When used through pagination in core module: `null` means no limit, must be positive otherwise.
     */
    public var limit: Int? = null

    override fun toRequest(): AuditLogGetRequest = AuditLogGetRequest(userId, action, before, after, limit)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AuditLogGetRequestBuilder

        if (userId != other.userId) return false
        if (action != other.action) return false
        if (before != other.before) return false
        if (after != other.after) return false
        if (limit != other.limit) return false

        return true
    }

    override fun hashCode(): Int {
        var result = userId?.hashCode() ?: 0
        result = 31 * result + (action?.hashCode() ?: 0)
        result = 31 * result + (before?.hashCode() ?: 0)
        result = 31 * result + (after?.hashCode() ?: 0)
        result = 31 * result + (limit ?: 0)
        return result
    }

}
