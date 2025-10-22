@file:Generate(
    STRING_KORD_ENUM,
    name = "NameplatePalette",
    docUrl = "https://discord.com/developers/docs/resources/user#nameplate-nameplate-structure",
    entries = [
        Entry("CRIMSON", stringValue = "crimson"),
        Entry("BERRY", stringValue = "berry"),
        Entry("SKY", stringValue = "sky"),
        Entry("TEAL", stringValue = "teal"),
        Entry("FOREST", stringValue = "forest"),
        Entry("BUBBLEGUM", stringValue = "bubble_gum"),
        Entry("VIOLET", stringValue = "violet"),
        Entry("COBALT", stringValue = "cobalt"),
        Entry("CLOVER", stringValue = "clover"),
        Entry("LEMON", stringValue = "lemon"),
        Entry("WHITE", stringValue = "white"),
    ]
)

package dev.kord.common.entity

import dev.kord.common.entity.optional.Optional
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.STRING_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordCollectible(
    val nameplate: Optional<DiscordNameplate> = Optional.Missing()
)

@Serializable
public data class DiscordNameplate(
    @SerialName("sku_id")
    val skuId: Snowflake,
    val asset: String,
    val label: String,
    val palette: NameplatePalette
)