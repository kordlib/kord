package dev.kord.rest.json.request

import dev.kord.common.entity.AuditLogEvent
import dev.kord.common.entity.Snowflake

data class AuditLogGetRequest(
    val userId: Snowflake? = null,
    val action: AuditLogEvent? = null,
    val before: Snowflake? = null,
    val limit: Int = 50,
)
