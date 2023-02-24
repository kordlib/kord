package dev.kord.common

import java.util.Locale as JLocale


/**
 * Converts this into a [JLocale].
 */
public fun Locale.asJavaLocale(): JLocale = JLocale(language, country ?: "")

/**
 * Converts this into a [Locale].
 */
public val JLocale.kLocale: Locale
    get() = Locale(language, country.ifBlank { null })
