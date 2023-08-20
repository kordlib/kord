package dev.kord.ksp.generation.bitflags

import com.squareup.kotlinpoet.KModifier.OPERATOR
import com.squareup.kotlinpoet.KModifier.PUBLIC
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import dev.kord.ksp.addFunction
import dev.kord.ksp.generation.GenerationEntity.BitFlags
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.BIT_SET
import dev.kord.ksp.generation.GenerationEntity.BitFlags.ValueType.INT
import dev.kord.ksp.generation.shared.GenerationContext
import dev.kord.ksp.returns

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addContains(parameterName: String, parameterType: TypeName) = addFunction("contains") {
    addKdoc("Checks if this instance of [%T] has all bits set that are set in [$parameterName].", collectionCN)
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns<Boolean>()
    addStatement(
        when (valueType) {
            INT -> "return this.$valueName·and·$parameterName.$valueName·==·$parameterName.$valueName"
            BIT_SET -> "return $parameterName.$valueName·in·this.$valueName"
        }
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addPlus(parameterName: String, parameterType: TypeName) = addFunction("plus") {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in `this` and [$parameterName].",
        collectionCN,
    )
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(collectionCN)
    addStatement(
        when (valueType) {
            INT -> "return %T(this.$valueName·or·$parameterName.$valueName)"
            BIT_SET -> "return %T(this.$valueName·+·$parameterName.$valueName)"
        },
        collectionCN,
    )
}

context(BitFlags, GenerationContext)
internal fun TypeSpec.Builder.addMinus(parameterName: String, parameterType: TypeName) = addFunction("minus") {
    addKdoc(
        "Returns an instance of [%T] that has all bits set that are set in `this` except the bits that are set in " +
            "[$parameterName].",
        collectionCN,
    )
    addModifiers(PUBLIC, OPERATOR)
    addParameter(parameterName, parameterType)
    returns(collectionCN)
    addStatement(
        when (valueType) {
            INT -> "return %T(this.$valueName·and·$parameterName.$valueName.inv())"
            BIT_SET -> "return %T(this.$valueName·-·$parameterName.$valueName)"
        },
        collectionCN,
    )
}
