package dev.kord.core.cache.data

import dev.kord.common.Locale
import dev.kord.common.entity.DiscordApplication
import dev.kord.common.entity.DiscordApplicationRoleConnectionMetadataRecord
import dev.kord.common.entity.DiscordApplicationRoleConnectionMetadataRecordType
import dev.kord.common.entity.optional.Optional

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

public data class ApplicationRoleConnectionMetadataData(
    val type: DiscordApplicationRoleConnectionMetadataRecordType,
    val key: String,
    val name: String,
    val nameLocalizations: Optional<Map<Locale, String>> = Optional.Missing(),
    val description: String,
    val descriptionLocalizations: Optional<Map<Locale, String>> = Optional.Missing()
) {
    public companion object {
        public fun from(record: DiscordApplicationRoleConnectionMetadataRecord): ApplicationRoleConnectionMetadataData =
            ApplicationRoleConnectionMetadataData(
                record.type,
                record.key,
                record.name,
                record.nameLocalizations,
                record.description,
                record.descriptionLocalizations
            )
    }
}
