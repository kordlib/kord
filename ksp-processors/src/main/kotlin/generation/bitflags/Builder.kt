package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.DelicateKotlinPoetApi
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.*
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.EMPTY_BIT_SET
import dev.kord.ksp.generation.shared.GenerationContext

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addBuilder() = addClass(builderCN) {
    primaryConstructor {
        addParameter(valueName, valueCN) {
            when (valueType) {
                INT -> defaultValue("0")
                BIT_SET -> defaultValue("%M()", EMPTY_BIT_SET)
            }
        }
    }
    addProperty(valueName, valueCN, PRIVATE) {
        mutable(
            when (valueType) {
                INT -> true
                BIT_SET -> false
            }
        )
        initializer(valueName)
    }
    addUnaryPlus(receiver = entityCN)
    addUnaryPlus(receiver = collectionCN)
    addUnaryMinus(receiver = entityCN)
    addUnaryMinus(receiver = collectionCN)
    addFunction("build") {
        addKdoc(
            "Returns an instance of [%T] that has all bits set that are currently set in this [%T].",
            collectionCN, builderCN,
        )
        addModifiers(PUBLIC)
        returns(collectionCN)
        val valueCopy = when (valueType) {
            INT -> ""
            BIT_SET -> ".copy()"
        }
        addStatement("return %T($valueName$valueCopy)", collectionCN)
    }
    if (builderHadFlagsFunction) {
        addFunction("flags") {
            addKdoc("@suppress")
            @OptIn(DelicateKotlinPoetApi::class)
            addAnnotation(
                Deprecated(
                    "Renamed to 'build'",
                    ReplaceWith("this.build()", imports = emptyArray()),
                    DeprecationLevel.WARNING,
                )
            )
            returns(collectionCN)
            addStatement("return build()")
        }
    }
}

context(GenerationContext)
private val BitFlags.builder
    get() = builderCN.simpleName

context(BitFlags, GenerationContext)
private fun TypeSpec.Builder.addUnaryPlus(receiver: ClassName) = addFunction("unaryPlus") {
    addKdoc("Sets all bits in the [%T] that are set in this [%T].", builderCN, receiver)
    addModifiers(PUBLIC, OPERATOR)
    receiver(receiver)
    addStatement(
        when (valueType) {
            INT -> "this@$builder.$valueName·=·this@$builder.$valueName·or·this.$valueName"
            BIT_SET -> "this@$builder.$valueName.add(this.$valueName)"
        }
    )
}

context(BitFlags, GenerationContext)
private fun TypeSpec.Builder.addUnaryMinus(receiver: ClassName) = addFunction("unaryMinus") {
    addKdoc("Unsets all bits in the [%T] that are set in this [%T].", builderCN, receiver)
    addModifiers(PUBLIC, OPERATOR)
    receiver(receiver)
    addStatement(
        when (valueType) {
            INT -> "this@$builder.$valueName·=·this@$builder.$valueName·and·this.$valueName.inv()"
            BIT_SET -> "this@$builder.$valueName.remove(this.$valueName)"
        }
    )
}
