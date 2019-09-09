package com.gitlab.kordlib.rest.json

import com.gitlab.kordlib.rest.json.response.AuditLogResponse
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object AuditLogResponseTest {

    @Test
    fun `AuditLogResponseSerialization serialization`() {

        val json = file("auditlog")
            val log = Json.parse<AuditLogResponse>(AuditLogResponse.serializer(), json)


    }

}