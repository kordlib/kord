@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.EMPTY_BIT_SET
import dev.kord.ksp.generation.shared.GenerationContext

context(flags: BitFlags, context: GenerationContext)
internal fun TypeSpec.Builder.addBuilder() = addClass(flags.builderCN) {
    primaryConstructor {
        addParameter(flags.valueName, context.valueCN) {
            when (flags.valueType) {
                INT -> defaultValue("0")
                BIT_SET -> defaultValue("%M()", EMPTY_BIT_SET)
            }
        }
    }
    addProperty(flags.valueName, context.valueCN, PRIVATE) {
        mutable(
            when (flags.valueType) {
                INT -> true
                BIT_SET -> false
            }
        )
        initializer(flags.valueName)
    }
    addUnaryPlus(receiver = context.entityCN)
    addUnaryPlus(receiver = flags.collectionCN)
    addUnaryMinus(receiver = context.entityCN)
    addUnaryMinus(receiver = flags.collectionCN)
    addFunction("build") {
        addKdoc(
            "Returns an instance of [%T] that has all bits set that are currently set in this [%T].",
            flags.collectionCN, flags.builderCN,
        )
        addModifiers(PUBLIC)
        returns(flags.collectionCN)
        val valueCopy = when (flags.valueType) {
            INT -> ""
            BIT_SET -> ".copy()"
        }
        addStatement("return %T(${flags.valueName}$valueCopy)", flags.collectionCN)
    }
}

context(_: GenerationContext)
private val BitFlags.builder
    get() = builderCN.simpleName

context(entity: BitFlags, context: GenerationContext)
private fun TypeSpec.Builder.addUnaryPlus(receiver: ClassName) = addFunction("unaryPlus") {
    addKdoc("Sets all bits in the [%T] that are set in this [%T].", entity.builderCN, receiver)
    addModifiers(PUBLIC, OPERATOR)
    receiver(receiver)
    addStatement(
        when (entity.valueType) {
            INT -> "this@${entity.builder}.${entity.valueName}·=·this@${entity.builder}.${entity.valueName}·or·this.${entity.valueName}"
            BIT_SET -> "this@${entity.builder}.${entity.valueName}.add(this.${entity.valueName})"
        }
    )
}

context(entity: BitFlags, context: GenerationContext)
private fun TypeSpec.Builder.addUnaryMinus(receiver: ClassName) = addFunction("unaryMinus") {
    addKdoc("Unsets all bits in the [%T] that are set in this [%T].", entity.builderCN, receiver)
    addModifiers(PUBLIC, OPERATOR)
    receiver(receiver)
    addStatement(
        when (entity.valueType) {
            INT -> "this@${entity.builder}.${entity.valueName}·=·this@${entity.builder}.${entity.valueName}·and·this.${entity.valueName}.inv()"
            BIT_SET -> "this@${entity.builder}.${entity.valueName}.remove(this.${entity.valueName})"
        }
    )
}
