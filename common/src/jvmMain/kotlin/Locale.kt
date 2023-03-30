package dev.kord.common

/**
 * Converts this [dev.kord.common.Locale] into a [java.util.Locale].
 */
public fun Locale.asJavaLocale(): java.util.Locale = java.util.Locale(language, country ?: "")

/**
 * Converts this [java.util.Locale] into a [dev.kord.common.Locale].
 */
public val java.util.Locale.kLocale: Locale get() = Locale(language, country.ifBlank { null })
