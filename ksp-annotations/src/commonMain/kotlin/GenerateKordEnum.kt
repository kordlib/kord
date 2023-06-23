package dev.kord.ksp

import dev.kord.ksp.GenerateKordEnum.*
import dev.kord.ksp.GenerateKordEnum.ValueType.*
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
    /** URL to the Discord Developer Documentation for the kord enum. */
    val docUrl: String,
    /** [Entries][Entry] of the kord enum. */
    val entries: Array<Entry>,
    /** KDoc for the kord enum (optional). */
    val kDoc: String = "",
    /** Name of the value of the kord enum. */
    val valueName: String = "value",

    /** Generate a Discord bit flags builder. */
    val isFlags: Boolean = false,
    /** Optional [BitFlagDescription] when [isFlags] is `true`. */
    val bitFlagsDescriptor: BitFlagDescription = BitFlagDescription(),
    /** Whether to add an "All" flag combining all flags into one. */
    val hasCombinerFlag: Boolean = false,
    /** Additional imports (e.g. for KDoc). */
    val additionalImports: Array<String> = [],
) {
    enum class ValueType { INT, STRING, BITSET }

    @Retention(SOURCE)
    @Target() // only use as argument for `@GenerateKordEnum(...)`
    annotation class Entry(
        /** Name of the entry. */
        val name: String,
        /** [Int] value of the entry. Only set this if [GenerateKordEnum.valueType] is [INT]. */
        val intValue: Int = 0,
        /** [String] value of the entry. Only set this if [GenerateKordEnum.valueType] is [STRING]. */
        val stringValue: String = "",
        /** [Long] value of the entry. Only set this if [GenerateKordEnum.valueType] is [BITSET]. */
        val longValue: Long = 0,
        /** KDoc for the entry (optional). */
        val kDoc: String = "",
        /** [Deprecated] annotation to mark this entry as deprecated. */
        val deprecated: Deprecated = Deprecated(""),
        /** Additional annotations to add to this entry. */
        val additionalOptInMarkerAnnotations: Array<String> = [],
    )

    /**
     * Description of the `flags` field using the generated enum.
     *
     * @property objectName the typical name of the object using this enum
     * @property flagsFieldName the name of the  "flags field"
     * @property article the article for [name]
     * @property name the name used in documentation
     */
    @Retention(SOURCE)
    @Target() // Only used as parameter
    annotation class BitFlagDescription(
        val objectName: String = "obj",
        val flagsFieldName: String = "flags",
        val article: String = "a",
        val name: String = "flag",
    )
}
