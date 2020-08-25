package com.gitlab.kordlib.rest.json

import com.gitlab.kordlib.common.annotation.KordUnstableApi
import com.gitlab.kordlib.rest.json.response.AuditLogResponse
import kotlinx.serialization.json.Json
import kotlinx.serialization.parse
import org.junit.jupiter.api.Test

@OptIn(KordUnstableApi::class)
object AuditLogResponseTest {

    @Test
    fun `AuditLogResponseSerialization serialization`() {

        val json = file("auditlog")
            val log = Json.decodeFromString(AuditLogResponse.serializer(), json)


    }

}