package dev.kord.gateway.json

import dev.kord.test.file
import kotlin.test.assertEquals

suspend fun readFile(prefix: String, name: String) = file("gateway", "json/$prefix/$name.json")

infix fun <T> T.shouldBe(that: T) {
    assertEquals(that, this)
}
