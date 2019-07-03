package com.gitlab.hopebaron.rest.json

import com.gitlab.hopebaron.common.entity.Overwrite
import com.gitlab.hopebaron.rest.json.response.AuditLogResponse
import kotlinx.serialization.internal.ArrayListSerializer
import kotlinx.serialization.internal.NullableSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonArrayBuilder
import kotlinx.serialization.json.jsonArray
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