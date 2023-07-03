package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.OPEN
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmField
import com.squareup.kotlinpoet.jvm.jvmStatic
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.GenerationContext
import kotlin.DeprecationLevel.HIDDEN

// TODO bump LEVEL and remove this file eventually
private val LEVEL = DeprecationLevel.WARNING
private val CLASS = ClassName("dev.kord.common", "Class")
private val JAVA = MemberName("dev.kord.common", "java")

context(BitFlags, GenerationContext)
@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeSpec.Builder.addDeprecatedEntityEnumArtifacts() {
    val deprecatedWithoutReplacement =
        Deprecated("$entityName is no longer an enum class. Deprecated without replacement.", level = LEVEL)
    val suppress = Suppress("DeprecatedCallableAddReplaceWith")
    addFunction("name") {
        addKdoc("@suppress")
        addAnnotation(suppress)
        addAnnotation(deprecatedWithoutReplacement)
        returns<String>()
        addStatement("return this::class.simpleName!!")
    }
    addFunction("ordinal") {
        addKdoc("@suppress")
        addAnnotation(suppress)
        addAnnotation(deprecatedWithoutReplacement)
        returns<Int>()
        withControlFlow("return when·(this)") {
            entries.forEachIndexed { index, entry ->
                addStatement("${entry.name}·->·%L", index)
            }
            addStatement("is·Unknown·->·Int.MAX_VALUE")
        }
    }
    addFunction("getDeclaringClass") {
        addKdoc("@suppress")
        addAnnotation(
            Deprecated(
                "$entityName is no longer an enum class.",
                ReplaceWith("$entityName::class.java", entityCN.canonicalName),
                LEVEL,
            )
        )
        returns(CLASS.parameterizedBy(entityCN).copy(nullable = true))
        addStatement("return $entityName::class.%M", JAVA)
    }
}

context(BitFlags, GenerationContext)
@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeSpec.Builder.addDeprecatedEntityCompanionObjectEnumArtifacts() {
    entries.forEach { entry ->
        addProperty(entry.name, entityCN) {
            addAnnotation(Deprecated("Binary compatibility", level = HIDDEN))
            jvmField()
            initializer(entry.name)
        }
    }
    addFunction("valueOf") {
        addKdoc("@suppress")
        addAnnotation(Suppress("NON_FINAL_MEMBER_IN_OBJECT", "DeprecatedCallableAddReplaceWith"))
        addAnnotation(
            Deprecated("$entityName is no longer an enum class. Deprecated without replacement.", level = LEVEL)
        )
        jvmStatic()
        addModifiers(OPEN)
        addParameter<String>("name")
        returns(entityCN)
        withControlFlow("return when·(name)") {
            entries.forEach { entry ->
                addStatement("%S·->·${entry.name}", entry.name)
            }
            addStatement("else·->·throw·IllegalArgumentException(name)")
        }
    }
    addFunction("values") {
        addKdoc("@suppress")
        addAnnotation(Suppress("NON_FINAL_MEMBER_IN_OBJECT"))
        addAnnotation(
            Deprecated(
                "$entityName is no longer an enum class.",
                ReplaceWith("$entityName.entries.toTypedArray()", entityCN.canonicalName),
                LEVEL,
            )
        )
        jvmStatic()
        addModifiers(OPEN)
        returns(ARRAY.parameterizedBy(entityCN))
        addStatement("return entries.toTypedArray()")
    }
}
