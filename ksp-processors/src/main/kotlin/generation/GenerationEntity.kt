package dev.kord.ksp.generation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import com.squareup.kotlinpoet.ClassName
import dev.kord.ksp.AnnotationArguments
import dev.kord.ksp.AnnotationArguments.Companion.arguments
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.*
import dev.kord.ksp.Generate.EntityType.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import kotlin.DeprecationLevel.WARNING
import kotlin.reflect.KProperty1

/** Mapping of [Generate] that is easier to work with in [GenerationProcessor]. */
internal sealed class GenerationEntity(
    val name: String,
    val kDoc: String?,
    val docUrl: String,
    val valueName: String,
    val entries: List<Entry>,
    val additionalImports: List<String>,
) {
    abstract val valueType: ValueType

    sealed interface ValueType

    class KordEnum(
        name: String, kDoc: String?, docUrl: String, valueName: String, entries: List<Entry>,
        additionalImports: List<String>,
        override val valueType: ValueType,
    ) : GenerationEntity(name, kDoc, docUrl, valueName, entries, additionalImports) {
        enum class ValueType : GenerationEntity.ValueType { INT, STRING }
    }

    class BitFlags(
        name: String, kDoc: String?, docUrl: String, entries: List<Entry>, additionalImports: List<String>,
        override val valueType: ValueType,
        val flagsDescriptor: BitFlagDescription,
        val hasCombinerFlag: Boolean,
    ) : GenerationEntity(name, kDoc, docUrl, valueName = "code", entries, additionalImports) {
        enum class ValueType : GenerationEntity.ValueType { INT, BIT_SET }
    }

    class Entry(
        val name: String,
        val kDoc: String?,
        val value: Comparable<*>,
        val deprecated: Deprecated?,
        val additionalOptInMarkerAnnotations: List<String>,
    )
}

internal class ProcessingContext(
    val packageName: String,
    val entityCN: ClassName,
    val valueCN: ClassName,
    val valueFormat: String,
    val relevantEntriesForSerializerAndCompanion: List<GenerationEntity.Entry>,
)

/**
 * Maps [KSAnnotation] for [Generate] to [GenerationEntity].
 *
 * Returns `null` if mapping fails.
 */
internal fun KSAnnotation.toGenerationEntityOrNull(logger: KSPLogger): GenerationEntity? {
    val args = arguments<Generate>()

    val entityType = args[Generate::entityType]!!
    val name = args[Generate::name]!!
    val docUrl = args[Generate::docUrl]!!
    val entries = args[Generate::entries]!!
    val kDoc = args[Generate::kDoc]?.toKDoc()
    val valueName = args[Generate::valueName] ?: "value"
    val bitFlagsDescriptor = args[Generate::bitFlagsDescriptor]?.toBitFlagDescription()
        ?: BitFlagDescription()
    val hasCombinerFlag = args[Generate::hasCombinerFlag] ?: false
    val additionalImports = args[Generate::additionalImports] ?: emptyList()

    fun areNotSpecified(vararg disallowedParameters: KProperty1<Generate, Any>) = disallowedParameters
        .filterNot { param -> args.isDefault(param) }
        .onEach { logger.error("${it.name} is not allowed for entityType $entityType", symbol = this) }
        .isEmpty()

    val validParameters = when (entityType) {
        INT_KORD_ENUM, STRING_KORD_ENUM -> areNotSpecified(Generate::bitFlagsDescriptor, Generate::hasCombinerFlag)
        INT_FLAGS, BIT_SET_FLAGS -> areNotSpecified(Generate::valueName)
    }

    val mappedEntries = entries
        .mapNotNull { it.toEntryOrNull(entityType, logger) }
        .takeIf { it.size == entries.size } ?: return null // there were errors while mapping entries

    return if (!validParameters) null else when (entityType) {
        INT_KORD_ENUM ->
            KordEnum(name, kDoc, docUrl, valueName, mappedEntries, additionalImports, KordEnum.ValueType.INT)
        STRING_KORD_ENUM ->
            KordEnum(name, kDoc, docUrl, valueName, mappedEntries, additionalImports, KordEnum.ValueType.STRING)
        INT_FLAGS -> BitFlags(
            name, kDoc, docUrl, mappedEntries, additionalImports, BitFlags.ValueType.INT, bitFlagsDescriptor,
            hasCombinerFlag,
        )
        BIT_SET_FLAGS -> BitFlags(
            name, kDoc, docUrl, mappedEntries, additionalImports, BitFlags.ValueType.BIT_SET, bitFlagsDescriptor,
            hasCombinerFlag,
        )
    }
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

private val ENTRY_VALUE_PARAMETERS = setOf(Entry::intValue, Entry::longValue, Entry::stringValue)

/**
 * Maps [KSAnnotation] for [Generate.Entry] to [GenerationEntity.Entry].
 *
 * Returns `null` if mapping fails.
 */
private fun KSAnnotation.toEntryOrNull(entityType: EntityType, logger: KSPLogger): GenerationEntity.Entry? {
    val args = arguments<Entry>()

    val name = args[Entry::name]!!
    val kDoc = args[Entry::kDoc]?.toKDoc()
    val deprecated = args[Entry::deprecated]?.toDeprecated()
        .takeUnless { args.isDefault(Entry::deprecated) }
    val additionalOptInMarkerAnnotations = args[Entry::additionalOptInMarkerAnnotations] ?: emptyList()

    fun <T : Any, R> KProperty1<Entry, T>.ifValid(getter: AnnotationArguments<Entry>.(KProperty1<Entry, T>) -> R): R? {
        val hasDisallowedParameters = ENTRY_VALUE_PARAMETERS
            .filterNot { param -> param == this || args.isDefault(param) }
            .onEach { logger.error("${it.name} is not allowed for entityType $entityType", symbol = this@KSAnnotation) }
            .isNotEmpty()
        val hasRequiredParameter = !args.isDefault(parameter = this)
        if (!hasRequiredParameter) {
            logger.error("Missing ${this.name} for entityType $entityType", symbol = this@KSAnnotation)
        }
        return if (hasDisallowedParameters || !hasRequiredParameter) null else args.getter(this)
    }

    val value = when (entityType) {
        INT_KORD_ENUM, INT_FLAGS -> Entry::intValue.ifValid { get(it) }
        STRING_KORD_ENUM -> Entry::stringValue.ifValid { get(it) }
        BIT_SET_FLAGS -> Entry::longValue.ifValid { get(it) }
    } ?: return null

    return GenerationEntity.Entry(name, kDoc, value, deprecated, additionalOptInMarkerAnnotations)
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
