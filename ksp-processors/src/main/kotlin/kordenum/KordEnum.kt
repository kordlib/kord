package dev.kord.ksp.kordenum

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import dev.kord.ksp.AnnotationArguments.Companion.arguments
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE
import dev.kord.ksp.kordenum.KordEnum.Entry
import kotlin.DeprecationLevel.WARNING

/** Mapping of [GenerateKordEnum] that is easier to work with in [KordEnumProcessor]. */
internal class KordEnum(
    val name: String,
    val kDoc: String?,
    val docUrl: String,
    val valueType: ValueType,
    val valueName: String,
    val entries: List<Entry>,

    // for migration purposes, TODO remove eventually
    val valuesPropertyName: String?,
    val valuesPropertyType: ValuesPropertyType,
    val deprecatedSerializerName: String?,
) {
    class Entry(
        val name: String,
        val kDoc: String?,
        val value: Comparable<*>,
        val deprecated: Deprecated?,
    )
}

/**
 * Maps [KSAnnotation] for [GenerateKordEnum] to [KordEnum].
 *
 * Returns `null` if mapping fails.
 */
internal fun KSAnnotation.toKordEnumOrNull(logger: KSPLogger): KordEnum? {
    val args = arguments<GenerateKordEnum>()

    val name = args[GenerateKordEnum::name]!!
    val valueType = args[GenerateKordEnum::valueType]!!
    val docUrl = args[GenerateKordEnum::docUrl]!!
    val entries = args[GenerateKordEnum::entries]!!
    val kDoc = args[GenerateKordEnum::kDoc]?.toKDoc()
    val valueName = args[GenerateKordEnum::valueName] ?: "value"

    val valuesPropertyName = args[GenerateKordEnum::valuesPropertyName]?.ifEmpty { null }
    val valuesPropertyType = args[GenerateKordEnum::valuesPropertyType] ?: NONE
    if (valuesPropertyName != null) {
        if (valuesPropertyType == NONE) {
            logger.error("Didn't specify valuesPropertyType", symbol = this)
            return null
        }
    } else {
        if (valuesPropertyType != NONE) {
            logger.error("Specified valuesPropertyType", symbol = this)
            return null
        }
    }
    val deprecatedSerializerName = args[GenerateKordEnum::deprecatedSerializerName]?.ifEmpty { null }

    val mappedEntries = entries
        .mapNotNull { it.toEntryOrNull(valueType, logger) }
        .takeIf { it.size == entries.size } ?: return null // there were errors while mapping entries

    return KordEnum(
        name, kDoc, docUrl, valueType, valueName, mappedEntries,

        valuesPropertyName, valuesPropertyType, deprecatedSerializerName,
    )
}

private fun String.toKDoc() = trimIndent().ifBlank { null }

/**
 * Maps [KSAnnotation] for [GenerateKordEnum.Entry] to [Entry].
 *
 * Returns `null` if mapping fails.
 */
private fun KSAnnotation.toEntryOrNull(valueType: ValueType, logger: KSPLogger): Entry? {
    val args = arguments<GenerateKordEnum.Entry>()

    val name = args[GenerateKordEnum.Entry::name]!!
    val kDoc = args[GenerateKordEnum.Entry::kDoc]?.toKDoc()
    val deprecated = args[GenerateKordEnum.Entry::deprecated]?.toDeprecated()
        .takeUnless { args.isDefault(GenerateKordEnum.Entry::deprecated) }

    val value = when (valueType) {
        INT -> {
            if (!args.isDefault(GenerateKordEnum.Entry::stringValue)) {
                logger.error("Specified stringValue for valueType $valueType", symbol = this)
                return null
            }
            if (args.isDefault(GenerateKordEnum.Entry::intValue)) {
                logger.error("Didn't specify intValue for valueType $valueType", symbol = this)
                return null
            }
            args[GenerateKordEnum.Entry::intValue]!!
        }
        STRING -> {
            if (!args.isDefault(GenerateKordEnum.Entry::intValue)) {
                logger.error("Specified intValue for valueType $valueType", symbol = this)
                return null
            }
            if (args.isDefault(GenerateKordEnum.Entry::stringValue)) {
                logger.error("Didn't specify stringValue for valueType $valueType", symbol = this)
                return null
            }
            args[GenerateKordEnum.Entry::stringValue]!!
        }
    }

    return Entry(name, kDoc, value, deprecated)
}

/** Maps [KSAnnotation] to [Deprecated]. */
private fun KSAnnotation.toDeprecated(): Deprecated {
    val args = arguments<Deprecated>()
    return Deprecated(
        message = args[Deprecated::message]!!,
        replaceWith = args[Deprecated::replaceWith]?.toReplaceWith() ?: ReplaceWith("", imports = emptyArray()),
        level = args[Deprecated::level] ?: WARNING,
    )
}

/** Maps [KSAnnotation] to [ReplaceWith]. */
private fun KSAnnotation.toReplaceWith(): ReplaceWith {
    val args = arguments<ReplaceWith>()
    return ReplaceWith(
        expression = args[ReplaceWith::expression]!!,
        imports = args[ReplaceWith::imports]!!.toTypedArray(),
    )
}
