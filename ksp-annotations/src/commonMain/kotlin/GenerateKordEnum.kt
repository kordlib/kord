package dev.kord.ksp

import dev.kord.ksp.GenerateKordEnum.Entry
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE
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

    // TODO remove eventually
    /** For migration purposes. */
    val valuesPropertyName: String = "",
    /** For migration purposes. */
    val valuesPropertyType: ValuesPropertyType = NONE,
    /** For migration purposes. */
    val deprecatedSerializerName: String = "",
) {
    enum class ValueType { INT, STRING }
    enum class ValuesPropertyType { NONE, SET }

    @Retention(SOURCE)
    @Target() // only use as argument for `@GenerateKordEnum(...)`
    annotation class Entry(
        /** Name of the entry. */
        val name: String,
        /** [Int] value of the entry. Only set this if [GenerateKordEnum.valueType] is [INT]. */
        val intValue: Int = 0,
        /** [String] value of the entry. Only set this if [GenerateKordEnum.valueType] is [STRING]. */
        val stringValue: String = "",
        /** KDoc for the entry (optional). */
        val kDoc: String = "",
        /** [Deprecated] annotation to mark this entry as deprecated. */
        val deprecated: Deprecated = Deprecated(""),
    )
}
