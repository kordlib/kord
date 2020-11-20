package com.gitlab.kordlib.rest.json.request

import com.gitlab.kordlib.common.entity.AuditLogEvent
import com.gitlab.kordlib.common.entity.Snowflake

data class AuditLogGetRequest(
        val userId: Snowflake? = null,
        val action: AuditLogEvent? = null,
        val before: Snowflake? = null,
        val limit: Int = 50,
)
