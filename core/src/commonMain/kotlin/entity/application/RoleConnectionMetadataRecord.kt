package dev.kord.core.entity.application

import dev.kord.common.Locale
import dev.kord.common.entity.DiscordApplicationRoleConnectionMetadataRecordType
import dev.kord.core.cache.data.ApplicationRoleConnectionMetadataData

/**
 * A representation of role connection metadata for an [application][Application].
 *
 * @property type [type][DiscordApplicationRoleConnectionMetadataRecordType] of metadata value
 * @property key dictionary key for the metadata field (must be a-z, 0-9, or _ characters; 1-50 characters)
 * @property name name of the metadata field (1-100 characters)
 * @property nameLocalizations with keys in available locales	translations of the name
 * @property description description of the metadata field (1-200 characters)
 * @property descriptionLocalizations with keys in available locales	translations of the description
 */
public class RoleConnectionMetadataRecord(public val data: ApplicationRoleConnectionMetadataData) {
    public val type: DiscordApplicationRoleConnectionMetadataRecordType get() = data.type
    public val key: String get() = data.key
    public val name: String get() = data.name
    public val nameLocalizations: Map<Locale, String>
        get() = data.nameLocalizations.value ?: emptyMap()
    public val description: String get() = data.description
    public val descriptionLocalizations: Map<Locale, String>
        get() = data.nameLocalizations.value ?: emptyMap()
}
