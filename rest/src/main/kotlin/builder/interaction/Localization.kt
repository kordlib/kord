package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl

/**
 * Builder which has a localisable name.
 *
 * @property name the default name
 * @property nameLocalizations a [MutableMap] containing localized versions of [name]
 *
 * @see Locale
 */
@KordDsl
public interface LocalizedNameBuilder {
    public val name: String?
    public var nameLocalizations: MutableMap<Locale, String>?

    /**
     * Registers a localization of [name] in [locale].
     */
    public fun name(locale: Locale, name: String) {
        if (nameLocalizations == null) nameLocalizations = mutableMapOf()
        nameLocalizations!![locale] = name
    }
}

/**
 * Builder for creating an entity with a localized [name],
 */
@KordDsl
public interface LocalizedNameCreateBuilder : LocalizedNameBuilder {
    public override var name: String
}

/**
 * Builder for modifying an entity with a localized [name],
 */
@KordDsl
public interface LocalizedNameModifyBuilder : LocalizedNameBuilder {
    public override var name: String?
}

/**
 * Builder which has a localisable description.
 *
 * @property description the default name
 * @property descriptionLocalizations a [MutableMap] containing localized versions of [description]
 *
 * @see Locale
 */
@KordDsl
public interface LocalizedDescriptionBuilder {
    public val description: String?
    public var descriptionLocalizations: MutableMap<Locale, String>?

    /**
     * Registers a localization of [description] in [locale].
     */
    public fun description(locale: Locale, name: String) {
        if (descriptionLocalizations == null) descriptionLocalizations = mutableMapOf()
        descriptionLocalizations!![locale] = name
    }
}

/**
 * Builder for creating an entity with a localized [description],
 */
@KordDsl
public interface LocalizedDescriptionCreateBuilder : LocalizedDescriptionBuilder {
    public override var description: String
}

/**
 * Builder for modifying an entity with a localized [description],
 */
@KordDsl
public interface LocalizedDescriptionModifyBuilder : LocalizedDescriptionBuilder {
    public override var description: String?
}
