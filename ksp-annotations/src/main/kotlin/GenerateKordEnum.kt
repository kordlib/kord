package dev.kord.ksp

import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.GenerateKordEnum.ValueType.BITSET
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
    /** Url to the Discord Developer Documentation for the kord enum (optional). */
    val docUrl: String = "",
    /** Name of the value of the kord enum. */
    val valueName: String = "value",
    /** [entries] of the kord enum that are [Deprecated]. [Entry.deprecationMessage] is required for these. */
    val deprecatedEntries: Array<Entry> = [],

    // TODO remove eventually
    /** For migration purposes. */
    val valuesPropertyName: String = "",
    /** For migration purposes. */
    val valuesPropertyType: ValuesPropertyType = NONE,
    /** For migration purposes. */
    val deprecatedSerializerName: String = "",
    /**
     * Generate a Discord bit flags builder.
     */
    val isFlags: Boolean = false,
    /**
     * Optional [BitFlagDescription] when using [isFlags] is true
     */
    val bitFlagsDescriptor: BitFlagDescription = BitFlagDescription(),
    /**
     * Whether to add an "All" flag combinding all flags into one.
     */
    val hasCombinerFlag: Boolean = false
) {
    enum class ValueType { INT, STRING, BITSET }
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
        /** [Long] value of the entry. Only set this if [GenerateKordEnum.valueType] is [BITSET]. */
        val longValue: Long = DEFAULT_LONG_VALUE,
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

            /** Default value for [longValue] */
            const val DEFAULT_LONG_VALUE = 0L

            /** Default value for [stringValue]. */
            const val DEFAULT_STRING_VALUE = ""
        }
    }

    /**
     * Description of the `flags` field using the generated enum.
     *
     * @property objectName the typical name of the object using this enum
     * @property flagsFieldName the name of the  "flags field"
     */
    @Retention(SOURCE)
    @Target() // Only used as parameter
    annotation class BitFlagDescription(
        val objectName: String = "obj",
        val flagsFieldName: String = "flags"
    )
}
