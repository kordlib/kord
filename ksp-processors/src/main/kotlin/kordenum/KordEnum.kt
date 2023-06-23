package dev.kord.ksp.kordenum

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.ClassName
import dev.kord.ksp.AnnotationArguments.Companion.arguments
import dev.kord.ksp.GenerateKordEnum
import dev.kord.ksp.GenerateKordEnum.BitFlagDescription
import dev.kord.ksp.GenerateKordEnum.ValueType
import dev.kord.ksp.GenerateKordEnum.ValueType.*
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
    val isFlags: Boolean,
    val flagsDescriptor: BitFlagDescription,
    val hasCombinerFlag: Boolean,
    val additionalImports: List<String>,
) {
    class Entry(
        val name: String,
        val kDoc: String?,
        val value: Comparable<*>,
        val deprecated: Deprecated?,
        val additionalOptInMarkerAnnotations: List<String>,
    )
}

internal data class ProcessingContext(
    val packageName: String,
    val enumName: ClassName,
    val valueTypeName: ClassName,
    val encodingPostfix: String,
    val valueFormat: String,
    val relevantEntriesForSerializerAndCompanion: List<Entry>
)

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
    val isFlags = args[GenerateKordEnum::isFlags] ?: false
    val valueName = args[GenerateKordEnum::valueName].takeIf { !isFlags } ?: if (isFlags) "code" else "value"
    val bitFlagsDescriptor = args[GenerateKordEnum::bitFlagsDescriptor]?.toBitFlagDescription()
        ?: BitFlagDescription()
    val hasCombinerFlag = args[GenerateKordEnum::hasCombinerFlag] ?: false
    val additionalImports = args[GenerateKordEnum::additionalImports] ?: emptyList()

    val mappedEntries = entries
        .mapNotNull { it.toEntryOrNull(valueType, logger) }
        .takeIf { it.size == entries.size } ?: return null // there were errors while mapping entries

    return KordEnum(
        name, kDoc, docUrl, valueType, valueName, mappedEntries, isFlags, bitFlagsDescriptor, hasCombinerFlag,
        additionalImports,
    )
}

private fun String.toKDoc() = trimIndent().ifBlank { null }

private fun KSAnnotation.toBitFlagDescription(): BitFlagDescription {
    val args = arguments<BitFlagDescription>()

    val objectName = args[BitFlagDescription::objectName] ?: "obj"
    val flagsFieldName = args[BitFlagDescription::flagsFieldName] ?: "flags"
    val article = args[BitFlagDescription::article] ?: "a"
    val name = args[BitFlagDescription::name] ?: "flag"

    return BitFlagDescription(objectName, flagsFieldName, article, name)
}

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
    val additionalOptInMarkerAnnotations = args[GenerateKordEnum.Entry::additionalOptInMarkerAnnotations] ?: emptyList()

    val value = when (valueType) {
        INT -> {
            if (!args.isDefault(GenerateKordEnum.Entry::stringValue)) {
                logger.error("Specified stringValue for valueType $valueType", symbol = this)
                return null
            }
            if (!args.isDefault(GenerateKordEnum.Entry::longValue)) {
                logger.error("Specified longValue for valueType $valueType", symbol = this)
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
            if (!args.isDefault(GenerateKordEnum.Entry::longValue)) {
                logger.error("Specified longValue for valueType $valueType", symbol = this)
                return null
            }
            if (args.isDefault(GenerateKordEnum.Entry::stringValue)) {
                logger.error("Didn't specify stringValue for valueType $valueType", symbol = this)
                return null
            }
            args[GenerateKordEnum.Entry::stringValue]!!
        }
        BITSET -> {
            if (!args.isDefault(GenerateKordEnum.Entry::intValue)) {
                logger.error("Specified intValue for valueType $valueType", symbol = this)
                return null
            }
            if (!args.isDefault(GenerateKordEnum.Entry::stringValue)) {
                logger.error("Specified stringValue for valueType $valueType", symbol = this)
                return null
            }
            if (args.isDefault(GenerateKordEnum.Entry::longValue)) {
                logger.error("Didn't specify longValue for valueType $valueType", symbol = this)
                return null
            }
            args[GenerateKordEnum.Entry::longValue]!!
        }
    }

    return Entry(name, kDoc, value, deprecated, additionalOptInMarkerAnnotations)
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
