package dev.kord.ksp.generation

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSAnnotation
import dev.kord.ksp.AnnotationArguments.Companion.arguments
import dev.kord.ksp.Generate
import dev.kord.ksp.Generate.*
import dev.kord.ksp.Generate.EntityType.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.KordEnum
import kotlin.reflect.KProperty1

/** Mapping of [Generate] that is easier to work with in [GenerationProcessor]. */
internal sealed class GenerationEntity(
    val entityName: String,
    val kDoc: String?,
    val docUrl: String,
    val valueName: String,
    val entries: List<Entry>,
) {
    abstract val valueType: ValueType

    sealed interface ValueType

    class KordEnum(
        name: String, kDoc: String?, docUrl: String, valueName: String, entries: List<Entry>,
        override val valueType: ValueType,
    ) : GenerationEntity(name, kDoc, docUrl, valueName, entries) {
        enum class ValueType : GenerationEntity.ValueType { INT, STRING }
    }

    class BitFlags(
        name: String, kDoc: String?, docUrl: String, entries: List<Entry>,
        override val valueType: ValueType,
        val flagsDescriptor: BitFlagDescription,
        val hasCombinerFlag: Boolean,
    ) : GenerationEntity(name, kDoc, docUrl, valueName = "code", entries) {
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

private fun String.toKDoc() = trimIndent().ifBlank { null }

/**
 * Maps [Generate] to [GenerationEntity].
 *
 * Returns `null` if mapping fails.
 */
internal fun Generate.toGenerationEntityOrNull(logger: KSPLogger, annotation: KSAnnotation): GenerationEntity? {
    val args = annotation.arguments<Generate>()

    fun areNotSpecified(vararg disallowedParameters: KProperty1<Generate, Any>) = disallowedParameters
        .filterNot { param -> args.isDefault(param) }
        .onEach { logger.error("${it.name} is not allowed for entityType $entityType", annotation) }
        .isEmpty()

    val validParameters = when (entityType) {
        INT_KORD_ENUM, STRING_KORD_ENUM -> areNotSpecified(Generate::bitFlagsDescriptor, Generate::hasCombinerFlag)
        INT_FLAGS, BIT_SET_FLAGS -> areNotSpecified(Generate::valueName)
    }

    val entries = entries zip args[Generate::entries]!!
    val mappedEntries = entries.mapNotNull { (entry, annotation) ->
        entry.toGenerationEntityEntryOrNull(entityType, logger, annotation)
    }

    return if (!validParameters || mappedEntries.size != entries.size) {
        null
    } else {
        val kDoc = kDoc.toKDoc()
        when (entityType) {
            INT_KORD_ENUM -> KordEnum(name, kDoc, docUrl, valueName, mappedEntries, KordEnum.ValueType.INT)
            STRING_KORD_ENUM -> KordEnum(name, kDoc, docUrl, valueName, mappedEntries, KordEnum.ValueType.STRING)
            INT_FLAGS ->
                BitFlags(name, kDoc, docUrl, mappedEntries, BitFlags.ValueType.INT, bitFlagsDescriptor, hasCombinerFlag)
            BIT_SET_FLAGS -> BitFlags(
                name, kDoc, docUrl, mappedEntries, BitFlags.ValueType.BIT_SET, bitFlagsDescriptor, hasCombinerFlag,
            )
        }
    }
}

private val ENTRY_VALUE_PARAMETERS = setOf(Entry::intValue, Entry::longValue, Entry::stringValue)

/**
 * Maps [Generate.Entry] to [GenerationEntity.Entry].
 *
 * Returns `null` if mapping fails.
 */
private fun Entry.toGenerationEntityEntryOrNull(
    entityType: EntityType,
    logger: KSPLogger,
    annotation: KSAnnotation,
): GenerationEntity.Entry? {
    val args = annotation.arguments<Entry>()

    fun <T : Any, R> KProperty1<Entry, T>.ifValid(getter: () -> R): R? {
        val hasDisallowedParameters = ENTRY_VALUE_PARAMETERS
            .filterNot { param -> param == this || args.isDefault(param) }
            .onEach { logger.error("${it.name} is not allowed for entityType $entityType", annotation) }
            .isNotEmpty()
        val hasRequiredParameter = !args.isDefault(parameter = this)
        if (!hasRequiredParameter) {
            logger.error("Missing ${this.name} for entityType $entityType", annotation)
        }
        return if (hasDisallowedParameters || !hasRequiredParameter) null else getter()
    }

    val value = when (entityType) {
        INT_KORD_ENUM, INT_FLAGS -> Entry::intValue.ifValid { intValue }
        STRING_KORD_ENUM -> Entry::stringValue.ifValid { stringValue }
        BIT_SET_FLAGS -> Entry::longValue.ifValid { longValue }
    } ?: return null

    return GenerationEntity.Entry(
        name,
        kDoc.toKDoc(),
        value,
        // copy annotation, the proxy instance ksp creates does not implement
        // java.lang.annotation.Annotation.annotationType() which is needed for kotlinpoet
        deprecated
            .run { Deprecated(message, replaceWith.run { ReplaceWith(expression, *imports) }, level) }
            .takeUnless { args.isDefault(Entry::deprecated) },
        // because of https://github.com/google/ksp/pull/1330#issuecomment-1616066129
        if (args.isDefault(Entry::additionalOptInMarkerAnnotations)) {
            emptyList()
        } else {
            additionalOptInMarkerAnnotations.toList()
        },
    )
}
