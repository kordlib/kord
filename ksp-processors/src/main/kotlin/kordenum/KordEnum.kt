package dev.kord.ksp.kordenum

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSType
import dev.kord.ksp.AnnotationArguments.Companion.annotationArguments
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.INT
import dev.kord.ksp.GenerateKordEnum.ValueType.STRING
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE
import dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.SET
import dev.kord.ksp.kordenum.KordEnum.Entry
import kotlin.DeprecationLevel.*

private val NATURAL_ORDER: Comparator<Comparable<*>> = compareBy { it }

/** Mapping of [GenerateKordEnum] that is easier to work with in [KordEnumProcessor]. */
internal class KordEnum(
    val name: String,
    val kDoc: String?,
    val docUrl: String?,
    val valueType: ValueType,
    val valueName: String,
    val entries: List<Entry>,
    val deprecatedEntries: List<Entry>,

    // for migration purposes, TODO remove eventually
    val valuesPropertyName: String?,
    val valuesPropertyType: ValuesPropertyType,
    val deprecatedSerializerName: String?,
) {
    internal class Entry(
        val name: String,
        val kDoc: String?,
        val value: Comparable<*>,
        val isKordExperimental: Boolean,
        val isDeprecated: Boolean,
        val deprecationMessage: String,
        val replaceWith: ReplaceWith,
        val deprecationLevel: DeprecationLevel,
    ) : Comparable<Entry> {
        override fun compareTo(other: Entry) = with(NATURAL_ORDER) { compare(value, other.value) }
    }
}

/**
 * Maps [KSAnnotation] for [GenerateKordEnum] to [KordEnum].
 *
 * Returns `null` if mapping fails.
 */
internal fun KSAnnotation.toKordEnumOrNull(logger: KSPLogger): KordEnum? {
    val args = annotationArguments

    val name = args[GenerateKordEnum::name] as String
    val valueType = args[GenerateKordEnum::valueType].toValueType()
    val entries = args[GenerateKordEnum::entries] as List<*>
    val kDoc = args[GenerateKordEnum::kDoc].toKDoc()
    val docUrl = (args[GenerateKordEnum::docUrl] as String).ifBlank { null }
    val valueName = args[GenerateKordEnum::valueName] as String
    val deprecatedEntries = args[GenerateKordEnum::deprecatedEntries] as List<*>

    val valuesPropertyName = (args[GenerateKordEnum::valuesPropertyName] as String).ifEmpty { null }
    val valuesPropertyType = args[GenerateKordEnum::valuesPropertyType].toValuesPropertyType()
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
    val deprecatedSerializerName = (args[GenerateKordEnum::deprecatedSerializerName] as String).ifEmpty { null }

    return KordEnum(
        name, kDoc, docUrl, valueType, valueName,
        entries.map { it.toEntryOrNull(valueType, isDeprecated = false, logger) ?: return null },
        deprecatedEntries.map { it.toEntryOrNull(valueType, isDeprecated = true, logger) ?: return null },

        valuesPropertyName, valuesPropertyType, deprecatedSerializerName,
    )
}

/** Maps [KSType] to [ValueType]. */
private fun Any?.toValueType() = when (val name = (this as KSType).declaration.qualifiedName?.asString()) {
    "dev.kord.ksp.GenerateKordEnum.ValueType.INT" -> INT
    "dev.kord.ksp.GenerateKordEnum.ValueType.STRING" -> STRING
    else -> error("Unknown GenerateKordEnum.ValueType: $name")
}

private fun Any?.toKDoc() = (this as String).trimIndent().ifBlank { null }

/** Maps [KSType] to [ValuesPropertyType]. */
private fun Any?.toValuesPropertyType() = when (val name = (this as KSType).declaration.qualifiedName?.asString()) {
    "dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.NONE" -> NONE
    "dev.kord.ksp.GenerateKordEnum.ValuesPropertyType.SET" -> SET
    else -> error("Unknown GenerateKordEnum.ValuesPropertyType: $name")
}

/**
 * Maps [KSAnnotation] for [GenerateKordEnum.Entry] to [Entry].
 *
 * Returns `null` if mapping fails.
 */
private fun Any?.toEntryOrNull(valueType: ValueType, isDeprecated: Boolean, logger: KSPLogger): Entry? {
    val args = (this as KSAnnotation).annotationArguments

    val name = args[GenerateKordEnum.Entry::name] as String
    val intValue = args[GenerateKordEnum.Entry::intValue] as Int
    val stringValue = args[GenerateKordEnum.Entry::stringValue] as String
    val kDoc = args[GenerateKordEnum.Entry::kDoc].toKDoc()
    val isKordExperimental = args[GenerateKordEnum.Entry::isKordExperimental] as Boolean
    val deprecationMessage = args[GenerateKordEnum.Entry::deprecationMessage] as String
    val replaceWith = args[GenerateKordEnum.Entry::replaceWith].toReplaceWith()
    val deprecationLevel = args[GenerateKordEnum.Entry::deprecationLevel].toDeprecationLevel()

    val value = when (valueType) {
        INT -> {
            if (stringValue != GenerateKordEnum.Entry.DEFAULT_STRING_VALUE) {
                logger.error("Specified stringValue for valueType $valueType", symbol = this)
                return null
            }
            if (intValue == GenerateKordEnum.Entry.DEFAULT_INT_VALUE) {
                logger.error("Didn't specify intValue for valueType $valueType", symbol = this)
                return null
            }

            intValue
        }
        STRING -> {
            if (intValue != GenerateKordEnum.Entry.DEFAULT_INT_VALUE) {
                logger.error("Specified intValue for valueType $valueType", symbol = this)
                return null
            }
            if (stringValue == GenerateKordEnum.Entry.DEFAULT_STRING_VALUE) {
                logger.error("Didn't specify stringValue for valueType $valueType", symbol = this)
                return null
            }

            stringValue
        }
    }

    if (isDeprecated) {
        if (deprecationMessage.isBlank()) {
            logger.error("deprecationMessage is required", symbol = this)
            return null
        }
    } else {
        if (deprecationMessage.isNotEmpty()) {
            logger.error("deprecationMessage is not allowed", symbol = this)
            return null
        }
        if (replaceWith.expression.isNotEmpty() || replaceWith.imports.isNotEmpty()) {
            logger.error("replaceWith is not allowed", symbol = this)
            return null
        }
        if (deprecationLevel != WARNING) {
            logger.error("deprecationLevel is not allowed", symbol = this)
            return null
        }
    }

    return Entry(name, kDoc, value, isKordExperimental, isDeprecated, deprecationMessage, replaceWith, deprecationLevel)
}

/** Maps [KSAnnotation] to [ReplaceWith]. */
private fun Any?.toReplaceWith(): ReplaceWith {
    val args = (this as KSAnnotation).annotationArguments

    val expression = args[ReplaceWith::expression] as String
    val imports = @Suppress("UNCHECKED_CAST") (args[ReplaceWith::imports] as List<String>)

    return ReplaceWith(expression, *imports.toTypedArray())
}

/** Maps [KSType] to [DeprecationLevel]. */
private fun Any?.toDeprecationLevel() = when (val name = (this as KSType).declaration.qualifiedName?.asString()) {
    "kotlin.DeprecationLevel.WARNING" -> WARNING
    "kotlin.DeprecationLevel.ERROR" -> ERROR
    "kotlin.DeprecationLevel.HIDDEN" -> HIDDEN
    else -> error("Unknown DeprecationLevel: $name")
}
