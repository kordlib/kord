package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.jvm.jvmField
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.generation.shared.K_SERIALIZER

// TODO bump LEVEL and remove this file eventually
private val LEVEL = DeprecationLevel.ERROR

context(BitFlags, GenerationContext)
@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeSpec.Builder.addDeprecatedNewCompanion() {
    val newCompanion = collectionCN.nestedClass("NewCompanion")
    val deprecated = Deprecated(
        "Renamed to 'Companion'.",
        ReplaceWith("${collectionCN.simpleName}.Companion", imports = arrayOf(collectionCN.canonicalName)),
        LEVEL,
    )
    addCompanionObject {
        addProperty(newCompanion.simpleName, type = newCompanion) {
            addAnnotation(Suppress("DEPRECATION_ERROR"))
            addAnnotation(deprecated)
            jvmField()
            initializer("%T()", newCompanion)
        }
    }
    addClass(newCompanion) {
        addAnnotation(deprecated)
        primaryConstructor { addModifiers(KModifier.INTERNAL) }
        addFunction("serializer") {
            addModifiers(KModifier.PUBLIC)
            returns(K_SERIALIZER.parameterizedBy(collectionCN))
            addStatement("return %T.serializer()", collectionCN)
        }
    }
}
