@file:Generate(
    name = "DiscordApplicationRoleConnectionMetadataRecordType",
    docUrl = "https://discord.com/developers/docs/resources/application-role-connection-metadata#application-role-connection-metadata-object-application-role-connection-metadata-type",
    kDoc = "Type of [DiscordRoleConnectionMetadata] values",
    entityType = Generate.EntityType.INT_KORD_ENUM,
    entries = [
        Generate.Entry(
            name = "IntegerLessThanOrEqual",
            intValue = 1,
            kDoc = "The metadata value (integer) is less than or equal to the guild's configured value (integer)"
        ),
        Generate.Entry(
            name = "IntegerGreaterThanOrEqual",
            intValue = 2,
            kDoc = "The metadata value (integer) is greater than or equal to the guild's configured value (integer)"
        ),
        Generate.Entry(
            name = "IntegerEqual",
            intValue = 3,
            kDoc = "The metadata value (integer) is equal to the guild's configured value (integer)"
        ),
        Generate.Entry(
            name = "IntegerNotEqual",
            intValue = 4,
            kDoc = "The metadata value (integer) is not equal to the guild's configured value (integer)"
        ),
        Generate.Entry(
            name = "DateTimeLessThanOrEqual",
            intValue = 5,
            kDoc = "The metadata value (ISO8601 string) is less than or equal to the guild's configured value (integer; days before current date)"
        ),
        Generate.Entry(
            name = "DateTimeGreaterThanOrEqual",
            intValue = 6,
            kDoc = "The metadata value (ISO8601 string) is greater than or equal to the guild's configured value (integer; days before current date)"
        ),
        Generate.Entry(
            name = "BooleanEqual",
            intValue = 7,
            kDoc = "The metadata value (integer) is equal to the guild's configured value (integer; 1)"
        ),
        Generate.Entry(
            name = "BooleanNotEqual",
            intValue = 8,
            kDoc = "The metadata value (integer) is not equal to the guild's configured value (integer; 1)"
        )
    ]
)

package dev.kord.common.entity

import dev.kord.common.Locale
import dev.kord.common.entity.optional.Optional
import dev.kord.ksp.Generate
import kotlinx.serialization.Serializable

/**
 * A representation of role connection metadata for an [application][DiscordApplication].
 *
 * @property type [type][DiscordApplicationRoleConnectionMetadataRecordType] of metadata value
 * @property key dictionary key for the metadata field (must be a-z, 0-9, or _ characters; 1-50 characters)
 * @property name name of the metadata field (1-100 characters)
 * @property nameLocalizations with keys in available locales	translations of the name
 * @property description description of the metadata field (1-200 characters)
 * @property descriptionLocalizations with keys in available locales	translations of the description
 */
@Serializable
public data class DiscordApplicationRoleConnectionMetadataRecord(
    val type: DiscordApplicationRoleConnectionMetadataRecordType,
    val key: String,
    val name: String,
    val nameLocalizations: Optional<Map<Locale, String>> = Optional.Missing(),
    val description: String,
    val descriptionLocalizations: Optional<Map<Locale, String>> = Optional.Missing()
)
