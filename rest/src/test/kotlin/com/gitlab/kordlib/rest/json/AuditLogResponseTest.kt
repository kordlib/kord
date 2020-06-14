package com.gitlab.kordlib.rest.json

import com.gitlab.kordlib.rest.json.response.AuditLogResponse
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

@UnstableDefault
object AuditLogResponseTest {

    @Test
    fun `AuditLogResponseSerialization serialization`() {

        val json = file("auditlog")
            val log = Json.parse(AuditLogResponse.serializer(), json)


    }

}