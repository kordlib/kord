package dev.kord.rest.builder.interaction

import dev.kord.common.Locale
import dev.kord.common.annotation.KordDsl

/**
 * Builder that has a localizable name.
 *
 * @see Locale
 */
@KordDsl
public interface LocalizedNameBuilder {

    /** The default name. */
    public val name: String?

    /** A [MutableMap] containing localized versions of [name]. */
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
 * Builder for creating an entity with a localized [name].
 */
@KordDsl
public interface LocalizedNameCreateBuilder : LocalizedNameBuilder {
    override var name: String
}

/**
 * Builder for modifying an entity with a localized [name].
 */
@KordDsl
public interface LocalizedNameModifyBuilder : LocalizedNameBuilder {
    override var name: String?
}

/**
 * Builder that has a localizable description.
 *
 * @see Locale
 */
@KordDsl
public interface LocalizedDescriptionBuilder {

    /** The default description. */
    public val description: String?

    /** A [MutableMap] containing localized versions of [description]. */
    public var descriptionLocalizations: MutableMap<Locale, String>?

    /**
     * Registers a localization of [description] in [locale].
     */
    public fun description(locale: Locale, description: String) {
        if (descriptionLocalizations == null) descriptionLocalizations = mutableMapOf()
        descriptionLocalizations!![locale] = description
    }
}

/**
 * Builder for creating an entity with a localized [description].
 */
@KordDsl
public interface LocalizedDescriptionCreateBuilder : LocalizedDescriptionBuilder {
    override var description: String
}

/**
 * Builder for modifying an entity with a localized [description].
 */
@KordDsl
public interface LocalizedDescriptionModifyBuilder : LocalizedDescriptionBuilder {
    override var description: String?
}
