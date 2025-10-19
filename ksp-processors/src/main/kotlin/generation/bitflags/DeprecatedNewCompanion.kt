@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

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
private val LEVEL = DeprecationLevel.HIDDEN

context(entity: BitFlags, context: GenerationContext)
@OptIn(DelicateKotlinPoetApi::class)
internal fun TypeSpec.Builder.addDeprecatedNewCompanion() {
    val newCompanion = entity.collectionCN.nestedClass("NewCompanion")
    val deprecated = Deprecated(
        "Renamed to 'Companion'. This declaration will be removed in 0.17.0.",
        ReplaceWith(
            "${entity.collectionCN.simpleName}.Companion",
            imports = arrayOf(entity.collectionCN.canonicalName)
        ),
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
            returns(K_SERIALIZER.parameterizedBy(entity.collectionCN))
            addStatement("return %T.serializer()", entity.collectionCN)
        }
    }
}
