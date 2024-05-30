package dev.kord.ksp.generation.shared

import com.google.devtools.ksp.symbol.KSFile
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.FileSpec
import dev.kord.ksp.FileSpec
import dev.kord.ksp.addAnnotation
import dev.kord.ksp.generation.GenerationEntity

internal class GenerationContext(
    val entityCN: ClassName,
    val valueCN: ClassName,
    val valueFormat: String,
    val entriesDistinctByValue: List<GenerationEntity.Entry>,
)

internal fun GenerationEntity.fileSpecForGenerationEntity(
    originatingFile: KSFile,
    block: context(GenerationContext) FileSpec.Builder.() -> Unit,
): FileSpec {
    val packageName = originatingFile.packageName.asString()
    val entityCN = ClassName(packageName, entityName)
    val valueCN = valueType.toClassName()
    val valueFormat = valueType.toFormat()

    val entriesDistinctByValue = entries
        .groupBy { it.value } // one entry per unique value is relevant
        .map { (_, group) -> group.firstOrNull { it.deprecated == null } ?: group.first() }

    val context = GenerationContext(entityCN, valueCN, valueFormat, entriesDistinctByValue)

    return FileSpec(packageName, fileName = entityName) {
        indent("    ")
        addFileComment("THIS FILE IS AUTO-GENERATED, DO NOT EDIT!")
        addKotlinDefaultImports(includeJvm = false, includeJs = false)
        @OptIn(DelicateKotlinPoetApi::class) // `AnnotationSpec.get` is ok for `Suppress`
        addAnnotation(
            Suppress(
                "IncorrectFormatting",
                "ReplaceArrayOfWithLiteral",
                "SpellCheckingInspection",
                "GrazieInspection",
                "MemberVisibilityCanBePrivate",
            )
        )
        block(context, this)
    }
}
