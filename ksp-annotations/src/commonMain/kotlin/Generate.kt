package dev.kord.ksp

import dev.kord.ksp.Generate.*
import dev.kord.ksp.Generate.EntityType.*
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FILE

/** Generate an entity in the same package as this file. */
@Repeatable
@Retention(SOURCE)
@Target(FILE)
annotation class Generate(
    /** The type of entity to generate. */
    val entityType: EntityType,
    /** Name of the entity. */
    val name: String,
    /** URL to the Discord Developer Documentation for the entity. */
    val docUrl: String,
    /** [Entries][Entry] of the entity. */
    val entries: Array<Entry>,
    /** KDoc for the entity (optional). */
    val kDoc: String = "",
    /** Name of the value of the entity. */
    val valueName: String = "value",

    /** Optional [BitFlagDescription] when [entityType] is [INT_FLAGS] or [BIT_SET_FLAGS]. */
    val bitFlagsDescriptor: BitFlagDescription = BitFlagDescription(),

    // for migration only, will be removed eventually
    val wasEnum: Boolean = false,
    val collectionWasDataClass: Boolean = false,
    val hadFlagsProperty: Boolean = false,
    val flagsPropertyWasSet: Boolean = false,
) {
    enum class EntityType { INT_KORD_ENUM, STRING_KORD_ENUM, INT_FLAGS, BIT_SET_FLAGS }

    @Retention(SOURCE)
    @Target() // only used as argument for `@Generate(...)`
    annotation class Entry(
        /** Name of the entry. */
        val name: String,
        /** [Int] value of the entry. Only set this if [Generate.entityType] is [INT_KORD_ENUM]. */
        val intValue: Int = 0,
        /** [String] value of the entry. Only set this if [Generate.entityType] is [STRING_KORD_ENUM]. */
        val stringValue: String = "",
        /** Shift distance of the entry. Only set this if [Generate.entityType] is [INT_FLAGS] or [BIT_SET_FLAGS]. */
        val shift: Int = 0,
        /** KDoc for the entry (optional). */
        val kDoc: String = "",
        /** [Deprecated] annotation to mark this entry as deprecated. */
        val deprecated: Deprecated = Deprecated(""),
        /** Additional annotations to add to this entry. */
        val additionalOptInMarkerAnnotations: Array<String> = [],
    )

    /**
     * Description of the `flags` field using the generated flags.
     *
     * @property objectName the typical name of the object using this flags
     * @property flagsFieldName the name of the  "flags field"
     * @property article the article for [name]
     * @property name the name used in documentation
     */
    @Retention(SOURCE)
    @Target() // only used as argument for `@Generate(...)`
    annotation class BitFlagDescription(
        val objectName: String = "obj",
        val flagsFieldName: String = "flags",
        val article: String = "a",
        val name: String = "flag",
    )
}
