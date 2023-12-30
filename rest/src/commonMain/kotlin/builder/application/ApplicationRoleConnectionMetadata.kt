package dev.kord.rest.builder.application

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl
import dev.kord.common.entity.ApplicationRoleConnectionMetadataType
import dev.kord.common.entity.DiscordApplicationRoleConnectionMetadata
import dev.kord.common.entity.optional.Optional
import dev.kord.common.entity.optional.delegate.delegate
import dev.kord.common.entity.optional.mapCopy
import dev.kord.rest.builder.RequestBuilder
import dev.kord.rest.builder.interaction.LocalizedDescriptionCreateBuilder
import dev.kord.rest.builder.interaction.LocalizedNameCreateBuilder
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

@KordDsl
public class ApplicationRoleConnectionMetadataRecordsBuilder :
    RequestBuilder<List<DiscordApplicationRoleConnectionMetadata>> {

    public var records: MutableList<ApplicationRoleConnectionMetadataBuilder> = mutableListOf()

    /**
     * Adds a configured [ApplicationRoleConnectionMetadataBuilder] to [records].
     *
     * @param type The [type][ApplicationRoleConnectionMetadataType] of metadata value.
     * @param key The dictionary key for the metadata field (must be `a-z`, `0-9`, or `_` characters; 1-50 characters).
     * @param name The name of the metadata field (1-100 characters).
     * @param description The description of the metadata field (1-200 characters).
     */
    public inline fun record(
        type: ApplicationRoleConnectionMetadataType,
        key: String,
        name: String,
        description: String,
        builder: ApplicationRoleConnectionMetadataBuilder.() -> Unit = {},
    ) {
        contract { callsInPlace(builder, EXACTLY_ONCE) }
        records += ApplicationRoleConnectionMetadataBuilder(type, key, name, description).apply(builder)
    }

    override fun toRequest(): List<DiscordApplicationRoleConnectionMetadata> = records.map { it.toRequest() }
}

@KordDsl
public class ApplicationRoleConnectionMetadataBuilder(
    /** The [type][ApplicationRoleConnectionMetadataType] of metadata value. */
    public var type: ApplicationRoleConnectionMetadataType,
    /** The dictionary key for the metadata field (must be `a-z`, `0-9`, or `_` characters; 1-50 characters). */
    public var key: String,
    /** The name of the metadata field (1-100 characters). */
    override var name: String,
    /** The description of the metadata field (1-200 characters). */
    override var description: String
) : RequestBuilder<DiscordApplicationRoleConnectionMetadata>,
    LocalizedNameCreateBuilder,
    LocalizedDescriptionCreateBuilder {

    private var _nameLocalizations: Optional<MutableMap<Locale, String>> = Optional.Missing()
    override var nameLocalizations: MutableMap<Locale, String>? by ::_nameLocalizations.delegate()

    private var _descriptionLocalizations: Optional<MutableMap<Locale, String>> = Optional.Missing()
    override var descriptionLocalizations: MutableMap<Locale, String>? by ::_descriptionLocalizations.delegate()

    override fun toRequest(): DiscordApplicationRoleConnectionMetadata = DiscordApplicationRoleConnectionMetadata(
        type = type,
        key = key,
        name = name,
        nameLocalizations = _nameLocalizations.mapCopy(),
        description = description,
        descriptionLocalizations = _descriptionLocalizations.mapCopy(),
    )
}
