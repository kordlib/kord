package dev.kord.rest.json

import dev.kord.common.entity.DiscordAuditLog
import dev.kord.test.IgnoreOnSimulatorPlatforms
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.js.JsName
import kotlin.test.Test

@IgnoreOnSimulatorPlatforms
class AuditLogResponseTest {

    @Test
    @JsName("test1")
    fun `AuditLogResponseSerialization serialization`() = runTest {

        val json = file("auditlog")

        @Suppress("UNUSED_VARIABLE")
        val log = Json.decodeFromString(DiscordAuditLog.serializer(), json)


    }

}
