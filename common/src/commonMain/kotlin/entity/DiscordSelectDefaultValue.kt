@file:Generate(
    STRING_KORD_ENUM, name = "SelectDefaultValueType",
    docUrl = "https://discord.com/developers/docs/interactions/message-components#select-menu-object-select-default-value-structure",
    entries = [
        Entry("User", stringValue = "user"),
        Entry("Role", stringValue = "role"),
        Entry("Channel", stringValue = "channel"),
    ],
)

package dev.kord.common.entity

import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordSelectDefaultValue(
    val id: Snowflake,
    val type: SelectDefaultValueType,
)
