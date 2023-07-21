package dev.kord.core.entity.application

import dev.kord.common.Locale
import dev.kord.common.entity.ApplicationRoleConnectionMetadataType
import dev.kord.common.entity.DiscordApplicationRoleConnectionMetadata
import dev.kord.core.Kord
import dev.kord.core.KordObject
import dev.kord.core.entity.Application
import dev.kord.core.entity.Guild

/**
 * A representation of role connection metadata for an [Application].
 *
 * When a [Guild] has added a bot and that bot has configured its
 * [roleConnectionsVerificationUrl][Application.roleConnectionsVerificationUrl] (in the developer portal), the
 * application will render as a potential verification method in the guild's role verification configuration.
 *
 * If an application has configured role connection metadata, its metadata will appear in the role verification
 * configuration when the application has been added as a verification method to the role.
 *
 * When a user connects their account using the bot's
 * [roleConnectionsVerificationUrl][Application.roleConnectionsVerificationUrl], the bot will update a user's role
 * connection with metadata using the OAuth2 `role_connections.write` scope.
 */
public class ApplicationRoleConnectionMetadata(
    public val data: DiscordApplicationRoleConnectionMetadata,
    override val kord: Kord,
) : KordObject {

    /** The [type][ApplicationRoleConnectionMetadataType] of metadata value. */
    public val type: ApplicationRoleConnectionMetadataType get() = data.type

    /** The dictionary key for the metadata field. */
    public val key: String get() = data.key

    /** The name of the metadata field. */
    public val name: String get() = data.name

    /** The translations of the [name]. */
    public val nameLocalizations: Map<Locale, String> get() = data.nameLocalizations.value ?: emptyMap()

    /** The description of the metadata field. */
    public val description: String get() = data.description

    /** The translations of the [description]. */
    public val descriptionLocalizations: Map<Locale, String> get() = data.descriptionLocalizations.value ?: emptyMap()

    override fun toString(): String = "ApplicationRoleConnectionMetadata(data=$data, kord=$kord)"
}
