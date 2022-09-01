package dev.kord.ksp

import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE
import kotlin.DeprecationLevel.WARNING
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FILE

/** Generate a kord enum in the same package as this file. */
@Repeatable
@Retention(SOURCE)
@Target(FILE)
annotation class GenerateKordEnum(
    /** Name of the kord enum. */
    val name: String,
    /** [ValueType] of the kord enum. */
    val valueType: ValueType,
    /** [Entries][Entry] of the kord enum. */
    val entries: Array<Entry>,
    /** KDoc for the kord enum (optional). */
    val kDoc: String = "",
    /** Name of the value of the kord enum. */
    val valueName: String = "value",
    /** [entries] of the kord enum that are [Deprecated]. [Entry.deprecationMessage] is required for these. */
    val deprecatedEntries: Array<Entry> = [],

    // TODO remove eventually
    /** For migration purposes. */
    val valuesPropertyName: String = "",
    /** For migration purposes. */
    val valuesPropertyType: ValuesPropertyType = NONE,
) {
    enum class ValueType { INT, STRING }
    enum class ValuesPropertyType { NONE, SET }

    @Retention(SOURCE)
    @Target() // only use as argument for `@GenerateKordEnum(...)`
    annotation class Entry(
        /** Name of the entry. */
        val name: String,
        /** [Int] value of the entry. Only set this if [GenerateKordEnum.valueType] is [INT]. */
        val intValue: Int = DEFAULT_INT_VALUE,
        /** [String] value of the entry. Only set this if [GenerateKordEnum.valueType] is [STRING]. */
        val stringValue: String = DEFAULT_STRING_VALUE,
        /** KDoc for the entry (optional). */
        val kDoc: String = "",
        /** Whether to add `@KordExperimental` to this entry. */
        val isKordExperimental: Boolean = false,
        /** [Deprecated.message] for a [deprecated entry][GenerateKordEnum.deprecatedEntries]. */
        val deprecationMessage: String = "",
        /** [Deprecated.replaceWith] for a [deprecated entry][GenerateKordEnum.deprecatedEntries]. */
        val replaceWith: ReplaceWith = ReplaceWith(""),
        /** [Deprecated.level] for a [deprecated entry][GenerateKordEnum.deprecatedEntries]. */
        val deprecationLevel: DeprecationLevel = WARNING,
    ) {
        companion object {
            /** Default value for [intValue]. */
            const val DEFAULT_INT_VALUE = Int.MIN_VALUE // probably won't appear anywhere

            /** Default value for [stringValue]. */
            const val DEFAULT_STRING_VALUE = ""
        }
    }
}
