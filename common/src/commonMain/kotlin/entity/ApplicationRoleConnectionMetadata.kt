@file:Generate(
    INT_KORD_ENUM, name = "ApplicationRoleConnectionMetadataType",
    kDoc = "Each [ApplicationRoleConnectionMetadataType] offers a comparison operation that allows guilds to " +
        "configure role requirements based on metadata values stored by the bot. Bots specify a 'metadata value' for " +
        "each user and guilds specify the required 'guild's configured value' within the guild role settings.",
    docUrl = "https://discord.com/developers/docs/resources/application-role-connection-metadata#application-role-connection-metadata-object-application-role-connection-metadata-type",
    entries = [
        Entry(
            "IntegerLessThanOrEqual", intValue = 1,
            kDoc = "The metadata value (`integer`) is less than or equal to the guild's configured value (`integer`).",
        ),
        Entry(
            "IntegerGreaterThanOrEqual", intValue = 2,
            kDoc = "The metadata value (`integer`) is greater than or equal to the guild's configured value " +
                "(`integer`).",
        ),
        Entry(
            "IntegerEqual", intValue = 3,
            kDoc = "The metadata value (`integer`) is equal to the guild's configured value (`integer`).",
        ),
        Entry(
            "IntegerNotEqual", intValue = 4,
            kDoc = "The metadata value (`integer`) is not equal to the guild's configured value (`integer`).",
        ),
        Entry(
            "DateTimeLessThanOrEqual", intValue = 5,
            kDoc = "The metadata value (`ISO8601 string`) is less than or equal to the guild's configured value " +
                "(`integer`; `days before current date`).",
        ),
        Entry(
            "DateTimeGreaterThanOrEqual", intValue = 6,
            kDoc = "The metadata value (`ISO8601 string`) is greater than or equal to the guild's configured value " +
                "(`integer`; `days before current date`).",
        ),
        Entry(
            "BooleanEqual", intValue = 7,
            kDoc = "The metadata value (`integer`) is equal to the guild's configured value (`integer`; `1`).",
        ),
        Entry(
            "BooleanNotEqual", intValue = 8,
            kDoc = "The metadata value (`integer`) is not equal to the guild's configured value (`integer`; `1`).",
        ),
    ],
)

package dev.kord.common.entity

import dev.kord.common.Locale
import dev.kord.common.entity.optional.Optional
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.EntityType.INT_KORD_ENUM
import dev.kord.ksp.Generate.Entry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class DiscordApplicationRoleConnectionMetadata(
    val type: ApplicationRoleConnectionMetadataType,
    val key: String,
    val name: String,
    @SerialName("name_localizations")
    val nameLocalizations: Optional<Map<Locale, String>> = Optional.Missing(),
    val description: String,
    @SerialName("description_localizations")
    val descriptionLocalizations: Optional<Map<Locale, String>> = Optional.Missing(),
)
