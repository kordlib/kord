package dev.kord.rest.json

import dev.kord.common.entity.DiscordAuditLog
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test

object AuditLogResponseTest {

    @Test
    fun `AuditLogResponseSerialization serialization`() {

        val json = file("auditlog")
        @Suppress("UNUSED_VARIABLE")
        val log = Json.decodeFromString(DiscordAuditLog.serializer(), json)


    }

}