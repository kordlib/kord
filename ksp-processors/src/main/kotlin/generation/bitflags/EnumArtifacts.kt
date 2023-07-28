package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.jvm.jvmField
import com.squareup.kotlinpoet.jvm.jvmStatic
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.GenerationContext
import kotlin.DeprecationLevel.HIDDEN
import kotlin.enums.EnumEntries

// TODO bump LEVEL and ENUM_ENTRIES_LEVEL and remove this file eventually
private val LEVEL = DeprecationLevel.WARNING
private val ENUM_ENTRIES_LEVEL = DeprecationLevel.ERROR // deprecated before released, only present in snapshots
private val CLASS = ClassName("dev.kord.common", "Class")
private val JAVA = MemberName("dev.kord.common", "java")

context(BitFlags, GenerationContext)
@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeSpec.Builder.addDeprecatedEntityEnumArtifacts() {
    val deprecatedWithoutReplacement =
        Deprecated("$entityName is no longer an enum class. Deprecated without a replacement.", level = LEVEL)
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
            Deprecated("$entityName is no longer an enum class. Deprecated without a replacement.", level = LEVEL)
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
    val enumEntries = EnumEntries::class.asClassName().parameterizedBy(entityCN)
    addObject("EnumEntriesList") {
        addAnnotation(
            Suppress(
                "SEALED_INHERITOR_IN_DIFFERENT_MODULE",
                "SEALED_INHERITOR_IN_DIFFERENT_PACKAGE",
                "UPPER_BOUND_VIOLATED",
            )
        )
        addModifiers(PRIVATE)
        addSuperinterface(enumEntries)
        addSuperinterface(LIST.parameterizedBy(entityCN), delegate = CodeBlock.of("entries"))
        addFunction("equals") {
            addModifiers(OVERRIDE)
            addParameter<Any?>("other")
            returns<Boolean>()
            addStatement("return entries == other")
        }
        addFunction("hashCode") {
            addModifiers(OVERRIDE)
            returns<Int>()
            addStatement("return entries.hashCode()")
        }
        addFunction("toString") {
            addModifiers(OVERRIDE)
            returns<String>()
            addStatement("return entries.toString()")
        }
    }
    addFunction("getEntries") {
        addKdoc("@suppress")
        addAnnotation(Suppress("NON_FINAL_MEMBER_IN_OBJECT", "UPPER_BOUND_VIOLATED"))
        addAnnotation(
            Deprecated(
                "$entityName is no longer an enum class.",
                ReplaceWith("$entityName.entries", entityCN.canonicalName),
                ENUM_ENTRIES_LEVEL,
            )
        )
        jvmStatic()
        addModifiers(OPEN)
        returns(enumEntries)
        addStatement("return EnumEntriesList")
    }
}
