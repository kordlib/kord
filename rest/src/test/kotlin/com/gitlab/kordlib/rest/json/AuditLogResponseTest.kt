package com.gitlab.kordlib.rest.json

import com.gitlab.kordlib.rest.json.response.AuditLogResponse
import kotlinx.serialization.json.Json
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object AuditLogResponseTest : Spek({

    describe("audit log response") {
        val json = file("auditlog")

        it("deserializes without error") {
            val log = Json.parse<AuditLogResponse>(AuditLogResponse.serializer(), json)
        }

    }


})