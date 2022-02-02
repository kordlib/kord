package dev.kord.rest.json.request

import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake

public data class AuditLogGetRequest(
    val userId: Snowflake? = null,
    val action: AuditLogEvent? = null,
    val before: Snowflake? = null,
    val limit: Int? = null,
)
